package com.fulu.game.core.service.impl.push;

import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.UserScoreEnum;
import com.fulu.game.common.enums.WechatTemplateEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.WxMaTemplateMessageVO;
import com.fulu.game.core.entity.vo.WechatFormidVO;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.WechatFormidService;
import com.fulu.game.core.service.aop.UserScore;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.core.service.queue.PushMsgQueue;
import com.xiaoleilu.hutool.date.DateField;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public abstract class MiniAppPushServiceImpl extends PushServiceImpl {
    @Autowired
    private PushMsgQueue pushMsgQueue;
    @Autowired
    private UserService userService;
    @Autowired
    private WechatFormidService wechatFormidService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    @Override
    public void start(Order order) {
        push(order.getUserId(), WechatTemplateMsgEnum.ORDER_TOUSER_START_SERVICE);
    }

    protected abstract void push(int userId, WechatTemplateMsgEnum wechatTemplateMsgEnum);

    /**
     * 通用模板推送消息
     *
     * @param userId
     * @param wechatTemplateMsgEnum
     * @param replaces
     */
    public void pushWechatTemplateMsg(int userId, WechatTemplateMsgEnum wechatTemplateMsgEnum, String... replaces) {
        String content = wechatTemplateMsgEnum.getContent();
        if (replaces != null && replaces.length > 0) {
            content = StrUtil.format(content, replaces);
        }
        String date = DateUtil.format(new Date(), "yyyy年MM月dd日 HH:mm");
        List<WxMaTemplateMessage.Data> dataList;
        switch (wechatTemplateMsgEnum.getTemplateId()) {
            case PLAY_LEAVE_MSG:
                dataList = CollectionUtil.newArrayList(new WxMaTemplateMessage.Data("keyword1", content), new WxMaTemplateMessage.Data("keyword2", date));
                break;
            default:
                dataList = CollectionUtil.newArrayList(new WxMaTemplateMessage.Data("keyword1", content), new WxMaTemplateMessage.Data("keyword2", date));
        }
        addTemplateMsg2Queue(wechatTemplateMsgEnum.getPlatform(), null, Arrays.asList(userId), wechatTemplateMsgEnum.getPage(), wechatTemplateMsgEnum.getTemplateId(), dataList);
    }


    /**
     * 批量写入推送模板消息
     *
     * @param pushId
     * @param userIds
     * @param page
     * @param wechatTemplateEnum
     * @param dataList
     */
    public synchronized void addTemplateMsg2Queue(int platform,
                                                  Integer pushId,
                                                  List<Integer> userIds,
                                                  String page,
                                                  WechatTemplateEnum wechatTemplateEnum,
                                                  List<WxMaTemplateMessage.Data> dataList) {
        //删除表里面过期的formId
        long startTime = System.currentTimeMillis();
        Date date = DateUtil.offset(new Date(), DateField.HOUR, (-24 * 7) + 1);
        wechatFormidService.deleteByExpireTime(date);
        long endTime = System.currentTimeMillis();
        log.info("pushTask:{}执行wechatFormidService.deleteByExpireTime方法耗时:{}", pushId, endTime - startTime);
        for (int i = 0; ; i = +1000) {
            List<WechatFormidVO> wechatFormidVOS = null;
            try {
                long findStartTime = System.currentTimeMillis();
                List<User> users = userService.findByUserIds(userIds, true);
                wechatFormidVOS = wechatFormidService.findByUserIds(platform, userIds, i, 1000);
                long findEndTime = System.currentTimeMillis();
                log.info("pushTask:{}执行wechatFormidService.findByUserIds:{}", pushId, findEndTime - findStartTime);
                if (wechatFormidVOS.isEmpty()) {
                    break;
                }
                List<String> formIds = new ArrayList<>();
                Map<Integer, String> userFormIds = new HashMap<>();
                //发送微信模板消息
                for (WechatFormidVO wechatFormidVO : wechatFormidVOS) {
                    WxMaTemplateMessage wxMaTemplateMessage = new WxMaTemplateMessage();
                    wxMaTemplateMessage.setTemplateId(wechatTemplateEnum.getType());
                    wxMaTemplateMessage.setToUser(wechatFormidVO.getOpenId());
                    wxMaTemplateMessage.setPage(page);
                    wxMaTemplateMessage.setFormId(wechatFormidVO.getFormId());
                    wxMaTemplateMessage.setData(dataList);
                    pushMsgQueue.addTemplateMessage(new WxMaTemplateMessageVO(platform, pushId, wxMaTemplateMessage));
                    formIds.add(wechatFormidVO.getFormId());
                    userFormIds.put(wechatFormidVO.getUserId(), wechatFormidVO.getFormId());
                }
                //没有有效formId的user进行短信补发
                sendSMSIfFormIdInVaild(userFormIds, users, dataList);
                //删除已经发过的formId
                if (formIds.size() > 0) {
                    long delStartTime = System.currentTimeMillis();
                    wechatFormidService.deleteFormIds(formIds.toArray(new String[]{}));
                    long delEndTime = System.currentTimeMillis();
                    log.info("pushTask:{}执行wechatFormidService.deleteFormIds方法耗时:{}", pushId, delEndTime - delStartTime);
                }
            } catch (Exception e) {
                log.error("批量写入推送模板消息异常wechatFormidVOS:{}", wechatFormidVOS);
                log.error("批量写入推送模板消息异常", e);
            }

        }
    }

    /**
     * 如果formid无效，补发短信
     *
     * @param userFormId
     * @param userList
     * @param dataList
     */
    private void sendSMSIfFormIdInVaild(Map<Integer, String> userFormId, List<User> userList, List<WxMaTemplateMessage.Data> dataList) {
        String content = null;
        if (CollectionUtil.isEmpty(dataList)) {
            return;
        }
        for (WxMaTemplateMessage.Data data : dataList) {
            if ("keyword1".equals(data.getName())) {
                content = data.getValue();
            }
        }
        if (StringUtils.isEmpty(content) || CollectionUtil.isEmpty(userList)) {
            return;
        }
        for (User user : userList) {
            String formId = userFormId.get(user.getId());
            if (StringUtils.isEmpty(formId)) {
                log.error("user或者formId为null无法给用户推送消息user:{};content:{};formId:{}", user, content, formId);
                if (StringUtils.isNotEmpty(user.getMobile())) {
                    Boolean flag = SMSUtil.sendLeaveInform(user.getMobile(), content, Constant.WEIXN_JUMP_URL);
                    if (!flag) {
                        log.error("留言通知发送短信失败:user.getMobile:{};content:{};", user.getMobile(), content);
                    } else {
                        log.info("留言通知发送短信成功:user.getMobile:{};content:{};", user.getMobile(), content);
                    }
                }
            }
            return;
        }
    }

    /**
     * 推送IM消息通知
     *
     * @param content
     * @param acceptImId 接收者IMid
     * @param imId       发送者IMid
     * @return
     */
    @UserScore(type = UserScoreEnum.IM_REPLY)
    public String pushIMWxTemplateMsg(String content,
                                      String acceptImId,
                                      String imId) {
        User acceptUser = userService.findByImId(acceptImId);
        if (acceptUser == null || acceptUser.getOpenId() == null) {
            log.error("acceptImId为:{}", acceptImId);
            throw new ServiceErrorException("AcceptIM不存在!");
        }
        //判断用户是否在线,在线状态不推送消息
        if (redisOpenService.hasKey(RedisKeyEnum.USER_ONLINE_KEY.generateKey(acceptUser.getId()))) {
            return "用户在线,不推送微信消息!";
        }
        User sendUser = userService.findByImId(imId);
        if (sendUser == null || sendUser.getOpenId() == null) {
            throw new ServiceErrorException("IM不存在!");
        }
        String timeStr = redisOpenService.get(RedisKeyEnum.WX_TEMPLATE_MSG.generateKey(imId + "|" + acceptImId));
        int time = (timeStr == null ? 0 : Integer.valueOf(timeStr));
        if (time >= 10) {
            return "推送次数太多不能推送!";
        }
        pushWechatTemplateMsg(acceptUser.getId(), WechatTemplateMsgEnum.IM_MSG_PUSH, sendUser.getNickname(), content);
        time += 1;
        //推送状态缓存两个小时
        redisOpenService.set(RedisKeyEnum.WX_TEMPLATE_MSG.generateKey(imId + "|" + acceptImId), time + "", Constant.TIME_MINUTES_FIFE);
        return "消息推送成功!";
    }
}
