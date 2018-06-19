package com.fulu.game.core.service.impl;

import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.queue.PushMsgQueue;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class WxTemplateMsgServiceImpl implements WxTemplateMsgService {

    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private UserService userService;
    @Autowired
    private WechatFormidService wechatFormidService;
    @Autowired
    private PushMsgQueue pushMsgQueue;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserInfoAuthService userInfoAuthService;


    private static final int LOCK_NUM = 10000;
    private List<Object> objects = new ArrayList<>(LOCK_NUM);

    {
        for (Integer i = 0; i < LOCK_NUM; i++) {
            objects.add(i);
        }
    }


    @Override
    @Async
    public void pushMarketOrder(String orderNo) {
        Order order = orderService.findByOrderNo(orderNo);
        log.info("推送集市订单:order:{};",order);
        Category category = categoryService.findById(order.getCategoryId());
        //查询所有符合推送条件的用户
        List<UserTechAuth> userTechAuthList = userTechAuthService.findNormalByCategory(order.getCategoryId());
        List<Integer> userIds = new ArrayList<>();
        for (UserTechAuth userTechAuth : userTechAuthList) {
            userIds.add(userTechAuth.getUserId());
        }
        List<User> userList = userService.findByUserIds(userIds);
        for (User user : userList) {
            if (!UserInfoAuthStatusEnum.VERIFIED.getType().equals(user.getUserInfoAuth()) || !UserStatusEnum.NORMAL.getType().equals(user.getStatus())) {
                continue;
            }
            UserInfoAuth userInfoAuth = userInfoAuthService.findByUserId(user.getId());
            //默认为30分钟
            Float pushTimeInterval = userInfoAuth.getPushTimeInterval();
            if(pushTimeInterval==null){
                pushTimeInterval = 30F;
            }
            //数据库设置永不推送
            if(pushTimeInterval.equals(0F)){
                log.info("推送集市订单:用户设置永不推送:userInfoAuth:{}",userInfoAuth);
                continue;
            }
            //时间间隔内已经推送过
            if(redisOpenService.hasKey(RedisKeyEnum.MARKET_ORDER_IS_PUSH.generateKey(user.getId()))){
               log.info("推送集市订单:该时间间隔内不能推送:userInfoAuth:{}",userInfoAuth);
               continue;
            }
            //推送订单消息
            pushWechatTemplateMsg(user.getId(),WechatTemplateMsgEnum.MARKET_ORDER_PUSH,category.getName());
            Long expire = new BigDecimal(pushTimeInterval).multiply(new BigDecimal(60)).longValue();
            redisOpenService.set(RedisKeyEnum.MARKET_ORDER_IS_PUSH.generateKey(user.getId()),orderNo,expire);
            log.info("推送集市订单完成:userInfoAuth:{},order:{}",userInfoAuth,order);
        }

    }


    public void adminPushWxTemplateMsg(int pushId, int userId, String page, String content) {
        String date = DateUtil.format(new Date(), "yyyy年MM月dd日 HH:mm");
        List<WxMaTemplateMessage.Data> dataList = CollectionUtil.newArrayList(new WxMaTemplateMessage.Data("keyword1", content),
                new WxMaTemplateMessage.Data("keyword2", date));
        pushWechatTemplateMsg(pushId, userId, page, WechatTemplateEnum.LEAVE_MSG, dataList);
    }


    @Override
    public void pushWechatTemplateMsg(int userId, WechatTemplateMsgEnum wechatTemplateMsgEnum, String... replaces) {
        String content = wechatTemplateMsgEnum.getContent();
        if (replaces != null && replaces.length > 0) {
            content = StrUtil.format(content, replaces);
        }
        String date = DateUtil.format(new Date(), "yyyy年MM月dd日 HH:mm");
        List<WxMaTemplateMessage.Data> dataList = null;
        switch (wechatTemplateMsgEnum.getTemplateId()) {
            case LEAVE_MSG:
                dataList = CollectionUtil.newArrayList(new WxMaTemplateMessage.Data("keyword1", content),
                        new WxMaTemplateMessage.Data("keyword2", date));
                break;
            default:
                dataList = CollectionUtil.newArrayList(new WxMaTemplateMessage.Data("keyword1", content),
                        new WxMaTemplateMessage.Data("keyword2", date));
        }
        pushWechatTemplateMsg(null, userId, wechatTemplateMsgEnum.getPage(), wechatTemplateMsgEnum.getTemplateId(), dataList);
    }


    /**
     * 写入队列推送消息
     *
     * @param pushId
     * @param userId
     * @param page
     * @param wechatTemplateEnum
     * @param dataList
     * @return
     */
    private void pushWechatTemplateMsg(Integer pushId,
                                       Integer userId,
                                       String page,
                                       WechatTemplateEnum wechatTemplateEnum,
                                       List<WxMaTemplateMessage.Data> dataList) {
        User user = userService.findById(userId);
        String formId = getWechatUserFormId(user.getId());
        if (formId == null) {
            log.error("formId为null,无法给用户推送消息。userId:{};page:{};templateId:{};content:{}", userId, page, wechatTemplateEnum.getType(), dataList);
            return;
        }
        WxMaTemplateMessage wxMaTemplateMessage = new WxMaTemplateMessage();
        wxMaTemplateMessage.setTemplateId(wechatTemplateEnum.getType());
        wxMaTemplateMessage.setToUser(user.getOpenId());
        wxMaTemplateMessage.setPage(page);
        wxMaTemplateMessage.setFormId(formId);
        wxMaTemplateMessage.setData(dataList);
        pushMsgQueue.addTemplateMessage(new WxMaTemplateMessageVO(pushId, wxMaTemplateMessage));
    }


    @Override
    public String pushIMWxTemplateMsg(String content,
                                      String acceptImId,
                                      String imId) {
        if (redisOpenService.hasKey(RedisKeyEnum.WX_TEMPLATE_MSG.generateKey(imId + "|" + acceptImId))) {
            return "消息已经推送过了!";
        }
        User acceptUser = userService.findByImId(acceptImId);
        if (acceptUser == null || acceptUser.getOpenId() == null) {
            throw new ServiceErrorException("AcceptIM不存在!");
        }
        User sendUser = userService.findByImId(imId);
        if (sendUser == null || sendUser.getOpenId() == null) {
            throw new ServiceErrorException("IM不存在!");
        }
        pushWechatTemplateMsg(acceptUser.getId(), WechatTemplateMsgEnum.IM_MSG_PUSH, sendUser.getNickname(), content);
        //推送状态缓存两个小时
        redisOpenService.set(RedisKeyEnum.WX_TEMPLATE_MSG.generateKey(imId + "|" + acceptImId), imId + "|" + acceptImId, Constant.TIME_MINUTES_FIFTEEN);
        return "消息推送成功!";
    }

    /**
     * 获取用户formId
     *
     * @param userId
     * @return
     */
    private String getWechatUserFormId(Integer userId) {
        List<WechatFormid> formidList = wechatFormidService.findInSevenDaysFormIdByUser(userId);
        if (formidList.isEmpty()) {
            return null;
        }
        synchronized (objects.get(userId % LOCK_NUM)) {
            WechatFormid formidObj = formidList.get(0);
            try {
                return formidObj.getFormId();
            } finally {
                wechatFormidService.deleteNotAvailableFormIds(formidObj);
            }
        }
    }


}
