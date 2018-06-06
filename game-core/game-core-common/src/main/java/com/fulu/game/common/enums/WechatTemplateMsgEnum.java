package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WechatTemplateMsgEnum {

    TECH_AUTH_AUDIT_ING("pages/a/verify/verify", WechatTemplateEnum.LEAVE_MSG, "您的好友{}成功帮您认证,还剩{}位好友认证即可成功通过审核."), //好友认证
    TECH_AUTH_AUDIT_SUCCESS("pages/a/setting/setting", WechatTemplateEnum.LEAVE_MSG, "恭喜您,成功通过审核,快快前往接单啦。"),
    TECH_AUTH_AUDIT_FAIL("pages/a/verify/verify", WechatTemplateEnum.LEAVE_MSG, "审核未通过:{},请前往重新提交审核."),

    GRANT_COUPON("pages/c/coupon/coupon?backHome=true", WechatTemplateEnum.LEAVE_MSG, "恭喜你,获得一张{}元优惠券"),

    ORDER_USER_PAY("pages/a/order/order", WechatTemplateEnum.LEAVE_MSG, "{}向您下单{},快快去赚钱"), //用户付款
    ORDER_SERVER_USER_CHECK("pages/c/order/order", WechatTemplateEnum.LEAVE_MSG, "您的{}订单已完成,请前往验收"), //打手验收订单
    ORDER_USER_APPEAL("pages/c/order/order", WechatTemplateEnum.LEAVE_MSG, "您的订单已成功申诉,客服正在处理,请耐心等待"),//用户申诉订单
    ORDER_SERVER_USER_APPEAL("pages/a/order/order", WechatTemplateEnum.LEAVE_MSG, "您的订单已被申诉,请尽快联系客服处理"),//用户申诉订单
    ORDER_USER_APPEAL_REFUND("pages/c/order/order", WechatTemplateEnum.LEAVE_MSG, "申诉成功,您支付金额将原路返回"),
    ORDER_SERVER_USER_APPEAL_REFUND("pages/a/order/order", WechatTemplateEnum.LEAVE_MSG, "申诉成功,您将无法获得该订单受益,还请继续努力"),
    ORDER_USER_APPEAL_COMPLETE("pages/c/order/order", WechatTemplateEnum.LEAVE_MSG, "申诉失败,您支付的金额将支付给陪玩师"),
    ORDER_SERVER_USER_APPEAL_COMPLETE("pages/a/order/order", WechatTemplateEnum.LEAVE_MSG, "申诉失败,订单金额将稍后到账"),
    ORDER_SYSTEM_USER_APPEAL_COMPLETE("pages/c/order/order", WechatTemplateEnum.LEAVE_MSG, "该订单已协商处理完成,协商结果请咨询客服"),
    ORDER_SYSTEM_SERVER_APPEAL_COMPLETE("pages/a/order/order", WechatTemplateEnum.LEAVE_MSG, "该订单已协商处理完成,协商结果请咨询客服"),

    IM_MSG_PUSH("pages/imsg/imsg", WechatTemplateEnum.LEAVE_MSG, "{}:{}");

    //跳转页面
    private String page;
    //模板代码
    private WechatTemplateEnum templateId;
    //内容
    private String content;


}
