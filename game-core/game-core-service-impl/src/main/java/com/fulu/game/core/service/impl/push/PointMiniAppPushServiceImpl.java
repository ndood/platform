package com.fulu.game.core.service.impl.push;

import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.fulu.game.common.enums.*;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.core.service.impl.UserTechAuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class PointMiniAppPushServiceImpl extends MiniAppPushServiceImpl {

    @Autowired
    private UserService userService;
    @Qualifier(value = "userTechAuthServiceImpl")
    @Autowired
    private UserTechAuthServiceImpl userTechAuthService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;


    /**
     * 微信模板消息推送
     * 现在微信模板消息只有两个模板：
     * 一种是留言通知，比如审核通过，im收到消息等
     *
     * @param userIds
     * @param wechatTemplateMsgEnum
     * @param replaces
     */
    @Override
    protected void push(List<Integer> userIds, WechatTemplateMsgEnum wechatTemplateMsgEnum, String... replaces) {
        WechatTemplateIdEnum templateId = WechatTemplateIdEnum.POINT_LEAVE_MSG;
        String page = wechatTemplateMsgEnum.getPage().getPlayPagePath();
        String content = wechatTemplateMsgEnum.getContent();
        if (replaces != null && replaces.length > 0) {
            content = StrUtil.format(content, replaces);
        }
        String date = DateUtil.format(new Date(), "yyyy年MM月dd日 HH:mm");
        List<WxMaTemplateMessage.Data> dataList = CollectionUtil.newArrayList(new WxMaTemplateMessage.Data("keyword1", content),
                new WxMaTemplateMessage.Data("keyword2", date));

        addTemplateMsg2Queue(PlatformEcoEnum.POINT.getType(), null, userIds, page, templateId, dataList);
    }

    /**
     * 微信模板消息推送
     * 现在微信模板消息只有两个模板：
     * 另一种是“服务进度通知”，比如订单流程中，已下单，已支付等状态变更都会有消息通知
     * 这两种目前区别在于是否跟订单相关
     *
     * @param userIds
     * @param order
     * @param wechatTemplateMsgEnum
     * @param replaces
     */
    @Override
    protected void push(List<Integer> userIds, Order order, WechatTemplateMsgEnum wechatTemplateMsgEnum, String... replaces) {
        WechatTemplateIdEnum templateId = WechatTemplateIdEnum.POINT_SERVICE_PROCESS_NOTICE;
        String content = wechatTemplateMsgEnum.getContent();
        if (replaces != null && replaces.length > 0) {
            content = StrUtil.format(content, replaces);
        }
        String date = DateUtil.format(DateUtil.date(), "yyyy年MM月dd日 HH:mm");
        List<WxMaTemplateMessage.Data> dataList;
        String serviceUserNickName = "";
        User serviceUser = userService.findById(order.getServiceUserId());
        if (serviceUser != null) {
            serviceUserNickName = serviceUser.getNickname();
        }
        dataList = CollectionUtil.newArrayList(
                //服务进度（订单状态）
                new WxMaTemplateMessage.Data("keyword1", OrderStatusEnum.getMsgByStatus(order.getStatus())),
                //订单编号
                new WxMaTemplateMessage.Data("keyword2", order.getOrderNo()),
                //订单金额
                new WxMaTemplateMessage.Data("keyword3", "￥" + order.getTotalMoney().toPlainString()),
                //通知时间
                new WxMaTemplateMessage.Data("keyword4", date),
                //服务人员（陪玩师姓名）
                new WxMaTemplateMessage.Data("keyword5", serviceUserNickName),
                //备注（消息模板内容）
                new WxMaTemplateMessage.Data("keyword6", content));
        addTemplateMsg2Queue(PlatformEcoEnum.POINT.getType(), null, userIds,
                wechatTemplateMsgEnum.getPage().getPointPagePath(), templateId, dataList);
    }


    /**
     * 推送上分订单通知
     *
     * @param order
     */
    public void pushPointOrder(Order order) {
        log.info("推送上分订单:order:{};", order);
        Category category = categoryService.findById(order.getCategoryId());
        //查询所有符合推送条件的用户
        List<UserTechAuth> userTechAuthList = userTechAuthService.findNormalByCategory(order.getCategoryId());
        List<Integer> userIds = new ArrayList<>();
        for (UserTechAuth userTechAuth : userTechAuthList) {
            userIds.add(userTechAuth.getUserId());
        }
        if (userIds.isEmpty()) {
            log.error("推送集市订单通知失败:没有符合条件的用户!");
            return;
        }

        List<UserVO> userList = userService.findVOByUserIds(userIds);
        for (UserVO user : userList) {
            if (!UserInfoAuthStatusEnum.VERIFIED.getType().equals(user.getUserInfoAuth()) || !UserStatusEnum.NORMAL.getType().equals(user.getStatus())) {
                continue;
            }
            //默认为1分钟
            Float pushTimeInterval = user.getPushTimeInterval();
            if (pushTimeInterval == null) {
                pushTimeInterval = 1F;
            }
            //数据库设置永不推送
            if (pushTimeInterval.equals(0F)) {
                log.info("推送集市订单:用户设置永不推送:user:{}", user);
                continue;
            }
            //时间间隔内已经推送过
            if (redisOpenService.hasKey(RedisKeyEnum.MARKET_ORDER_IS_PUSH.generateKey(user.getId()))) {
                log.info("推送集市订单:该时间间隔内不能推送:user:{}", user);
                continue;
            }
            //推送订单消息
            push(Collections.singletonList(user.getId()),
                    order,
                    WechatTemplateMsgEnum.POINT_ORDER_PUSH,
                    category.getName());
            Long expire = new BigDecimal(pushTimeInterval).multiply(new BigDecimal(60)).longValue();
            redisOpenService.set(RedisKeyEnum.MARKET_ORDER_IS_PUSH.generateKey(user.getId()), order.getOrderNo(), expire);
            log.info("推送集市订单完成:userInfoAuth:{},order:{}", user, order);
        }
    }

    @Override
    public void adminPush(PushMsg pushMsg, List<Integer> userIds, String page) {
        String date = DateUtil.format(new Date(), "yyyy年MM月dd日 HH:mm");
        List<WxMaTemplateMessage.Data> dataList = CollectionUtil.newArrayList(
                new WxMaTemplateMessage.Data("keyword1", pushMsg.getContent()),
                new WxMaTemplateMessage.Data("keyword2", date));
        addTemplateMsg2Queue(pushMsg.getPlatform(), pushMsg.getId(), userIds, page, WechatTemplateIdEnum.POINT_LEAVE_MSG, dataList);
    }

    /**
     * 客服仲裁，用户胜
     *
     * @param order
     */
    @Override
    public void appealUserWin(Order order) {
        List<Integer> userIds = Collections.singletonList(order.getUserId());
        push(userIds, order, WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_USER_WIN);
        push(userIds, order, WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_USER_WIN);
    }

    /**
     * 客服仲裁，陪玩师胜
     *
     * @param order
     */
    @Override
    public void appealServiceWin(Order order) {
        List<Integer> userIds = Collections.singletonList(order.getUserId());
        push(userIds, order, WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_SERVICE_WIN);
        push(userIds, order, WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_SERVICE_WIN);
    }

    /**
     * 客服仲裁协商处理
     *
     * @param order
     * @param msg
     */
    @Override
    public void appealNegotiate(Order order, String msg) {
        List<Integer> userIds = Collections.singletonList(order.getUserId());
        push(userIds, order, WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE, msg);
        push(userIds, order, WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE, msg);
    }

    /**
     * 发放优惠券
     *
     * @param userId
     * @param deduction
     */
    @Override
    public void grantCouponMsg(int userId, String deduction) {
        List<Integer> userIds = Collections.singletonList(userId);
        push(userIds, WechatTemplateMsgEnum.GRANT_COUPON, deduction);
    }

    /**
     * 陪玩师技能审核通过
     */
    @Override
    public void techAuthAuditSuccess(Integer userId) {
        List<Integer> userIds = Collections.singletonList(userId);
        push(userIds, WechatTemplateMsgEnum.TECH_AUTH_AUDIT_SUCCESS);
    }

    /**
     * 陪玩师技能审核通过
     */
    @Override
    public void techAuthAuditFail(Integer userId, String msg) {
        List<Integer> userIds = Collections.singletonList(userId);
        push(userIds, WechatTemplateMsgEnum.TECH_AUTH_AUDIT_FAIL, msg);
    }

    /**
     * 陪玩师个人信息审核不通过
     */
    @Override
    public void userInfoAuthFail(Integer userId, String msg) {
        List<Integer> userIds = Collections.singletonList(userId);
        push(userIds, WechatTemplateMsgEnum.USER_AUTH_INFO_REJECT, msg);
    }

    /**
     * 陪玩师个人信息审核通过
     *
     * @param userId
     */
    @Override
    public void userInfoAuthSuccess(Integer userId) {
        List<Integer> userIds = Collections.singletonList(userId);
        push(userIds, WechatTemplateMsgEnum.USER_AUTH_INFO_PASS);
    }

    /**
     * 同意协商
     *
     * @param order
     */
    @Override
    public void consultAgree(Order order) {
        List<Integer> userIds = Collections.singletonList(order.getUserId());
        push(userIds, order, WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT_CANCEL);
    }

    /**
     * 取消协商
     *
     * @param order
     */
    @Override
    public void consultCancel(Order order) {
        List<Integer> userIds = Collections.singletonList(order.getUserId());
        push(userIds, order, WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT_CANCEL);
    }

}
