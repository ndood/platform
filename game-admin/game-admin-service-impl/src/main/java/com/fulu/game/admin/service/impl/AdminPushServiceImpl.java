package com.fulu.game.admin.service.impl;

import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.enums.WechatTemplateIdEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.impl.push.PushServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminPushServiceImpl extends PushServiceImpl {


    /**
     * 客服仲裁，用户胜
     *
     * @param order
     */
    public void appealUserWin(Order order) {
        if (OrderTypeEnum.PLATFORM.getType().equals(order.getType())) {
            pushServiceProcessMsg(PlatformEcoEnum.PLAY.getType(),
                    order.getUserId(),
                    order,
                    WechatTemplateIdEnum.PLAY_SERVICE_PROCESS_NOTICE,
                    WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_USER_WIN);
            pushServiceProcessMsg(PlatformEcoEnum.PLAY.getType(),
                    order.getServiceUserId(),
                    order,
                    WechatTemplateIdEnum.PLAY_SERVICE_PROCESS_NOTICE,
                    WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_USER_WIN);
        } else if (OrderTypeEnum.POINT.getType().equals(order.getType())) {
            pushServiceProcessMsg(PlatformEcoEnum.POINT.getType(),
                    order.getUserId(),
                    order,
                    WechatTemplateIdEnum.POINT_SERVICE_PROCESS_NOTICE,
                    WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_USER_WIN);
            pushServiceProcessMsg(PlatformEcoEnum.POINT.getType(),
                    order.getServiceUserId(),
                    order,
                    WechatTemplateIdEnum.POINT_SERVICE_PROCESS_NOTICE,
                    WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_USER_WIN);
        } else if (OrderTypeEnum.H5.getType().equals(order.getType())) {
            pushWechatTemplateMsg(PlatformEcoEnum.POINT.getType(), order.getServiceUserId(), WechatTemplateIdEnum.POINT_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_USER_WIN.getPage().getPointPagePath(), WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_USER_WIN.getContent());
        }
    }

    /**
     * 客服仲裁，陪玩师胜
     *
     * @param order
     */
    public void appealServiceWin(Order order) {
        if (OrderTypeEnum.PLATFORM.getType().equals(order.getType())) {
            pushServiceProcessMsg(PlatformEcoEnum.PLAY.getType(),
                    order.getUserId(),
                    order,
                    WechatTemplateIdEnum.PLAY_SERVICE_PROCESS_NOTICE,
                    WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_SERVICE_WIN);
            pushServiceProcessMsg(PlatformEcoEnum.PLAY.getType(),
                    order.getServiceUserId(),
                    order,
                    WechatTemplateIdEnum.PLAY_SERVICE_PROCESS_NOTICE,
                    WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_SERVICE_WIN);
        } else if (OrderTypeEnum.POINT.getType().equals(order.getType())) {
            pushServiceProcessMsg(PlatformEcoEnum.POINT.getType(),
                    order.getUserId(),
                    order,
                    WechatTemplateIdEnum.POINT_SERVICE_PROCESS_NOTICE,
                    WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_SERVICE_WIN);
            pushServiceProcessMsg(PlatformEcoEnum.POINT.getType(),
                    order.getServiceUserId(),
                    order,
                    WechatTemplateIdEnum.POINT_SERVICE_PROCESS_NOTICE,
                    WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_SERVICE_WIN);
        } else if (OrderTypeEnum.H5.getType().equals(order.getType())) {
            //todo 给用户发送仲裁短信
            pushWechatTemplateMsg(PlatformEcoEnum.POINT.getType(), order.getServiceUserId(), WechatTemplateIdEnum.POINT_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_SERVICE_WIN.getPage().getPointPagePath(), WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_SERVICE_WIN.getContent());

        }
    }

    /**
     * 客服仲裁协商处理
     *
     * @param order
     * @param msg
     */
    public void appealNegotiate(Order order, String msg) {
        if (OrderTypeEnum.PLATFORM.getType().equals(order.getType())) {
            pushServiceProcessMsg(PlatformEcoEnum.PLAY.getType(),
                    order.getUserId(),
                    order,
                    WechatTemplateIdEnum.PLAY_SERVICE_PROCESS_NOTICE,
                    WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE,
                    msg);
            pushServiceProcessMsg(PlatformEcoEnum.PLAY.getType(),
                    order.getServiceUserId(),
                    order,
                    WechatTemplateIdEnum.PLAY_SERVICE_PROCESS_NOTICE,
                    WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE,
                    msg);
        } else if (OrderTypeEnum.POINT.getType().equals(order.getType())) {
            pushServiceProcessMsg(PlatformEcoEnum.POINT.getType(),
                    order.getUserId(),
                    order,
                    WechatTemplateIdEnum.POINT_SERVICE_PROCESS_NOTICE,
                    WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE,
                    msg);
            pushServiceProcessMsg(PlatformEcoEnum.POINT.getType(),
                    order.getServiceUserId(),
                    order,
                    WechatTemplateIdEnum.POINT_SERVICE_PROCESS_NOTICE,
                    WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE,
                    msg);
        } else if (OrderTypeEnum.H5.getType().equals(order.getType())) {
            //todo 给用户发送仲裁短信
            pushWechatTemplateMsg(PlatformEcoEnum.POINT.getType(), order.getServiceUserId(), WechatTemplateIdEnum.POINT_LEAVE_MSG, WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE.getPage().getPointPagePath(), WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE.getContent(), msg);
        }
    }

    /**
     * 发放优惠券
     *
     * @param userId
     * @param deduction
     */
    public void grantCouponMsg(int userId, String deduction) {
        pushWechatTemplateMsg(PlatformEcoEnum.PLAY.getType(), userId, WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.GRANT_COUPON.getPage().getPlayPagePath(), WechatTemplateMsgEnum.GRANT_COUPON.getContent(), deduction);
    }

    /**
     * 陪玩师技能审核通过
     */
    public void techAuthAuditSuccess(Integer userId) {
        pushWechatTemplateMsg(PlatformEcoEnum.PLAY.getType(), userId, WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.TECH_AUTH_AUDIT_SUCCESS.getPage().getPlayPagePath(), WechatTemplateMsgEnum.TECH_AUTH_AUDIT_SUCCESS.getContent());
    }

    /**
     * 陪玩师技能审核通过
     */
    public void techAuthAuditFail(Integer userId, String msg) {
        pushWechatTemplateMsg(PlatformEcoEnum.PLAY.getType(), userId, WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.TECH_AUTH_AUDIT_FAIL.getPage().getPlayPagePath(), WechatTemplateMsgEnum.TECH_AUTH_AUDIT_FAIL.getContent(), msg);
    }

    /**
     * 陪玩师个人信息审核不通过
     */
    public void userInfoAuthFail(Integer userId, String msg) {
        pushWechatTemplateMsg(PlatformEcoEnum.PLAY.getType(), userId, WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.USER_AUTH_INFO_REJECT.getPage().getPlayPagePath(), WechatTemplateMsgEnum.USER_AUTH_INFO_REJECT.getContent(), msg);
    }

    /**
     * 陪玩师个人信息审核通过
     *
     * @param userId
     */
    public void userInfoAuthSuccess(Integer userId) {
        pushWechatTemplateMsg(PlatformEcoEnum.PLAY.getType(), userId, WechatTemplateIdEnum.PLAY_LEAVE_MSG, WechatTemplateMsgEnum.USER_AUTH_INFO_PASS.getPage().getPlayPagePath(), WechatTemplateMsgEnum.USER_AUTH_INFO_PASS.getContent());
    }


}
