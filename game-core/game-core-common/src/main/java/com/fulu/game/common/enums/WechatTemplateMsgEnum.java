package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WechatTemplateMsgEnum {

    TECH_AUTH_AUDIT_ING(WechatPagePathEnum.SERVICE_USER_VERIFY, "您的好友{}成功帮您认证,还剩{}位好友认证即可成功通过审核."), //好友认证
    TECH_AUTH_AUDIT_SUCCESS(WechatPagePathEnum.ORDER_RECEIVING_SETTING, "恭喜您,成功通过审核,快快前往接单啦。"),
    TECH_AUTH_AUDIT_FAIL(WechatPagePathEnum.SERVICE_USER_VERIFY, "审核未通过:{},请前往重新提交审核."),
    GRANT_COUPON(WechatPagePathEnum.COUPON_LIST, "恭喜你,获得一张{}元优惠券"), //todo 多个模块都需要发优惠券通知这个时候，这个时候在service怎么注入
    USER_AUTH_INFO_PASS(WechatPagePathEnum.SERVICE_USER_VERIFY, "您提交申请的陪玩师个人资料已经审核通过，快去玩耍接单吧!"),
    USER_AUTH_INFO_REJECT(WechatPagePathEnum.SERVICE_USER_VERIFY, "您提交的陪玩师个人资料申请被驳回，理由:{}"),



    ORDER_TOUSER_REJECT_RECEIVE(WechatPagePathEnum.ORDER_LIST_PAGE, "【订单状态：订单取消】备注：陪玩师太忙了，为了不耽误您的游戏体验，拒绝了订单，望老板大人理解。"),//拒绝接单
    ORDER_TOUSER_AFFIRM_RECEIVE(WechatPagePathEnum.ORDER_LIST_PAGE, "【订单状态：等待服务】备注：陪玩师已经接单啦，一起谈谈什么时候开始呢？超过24小时未开始，订单将自动取消。"),//已经接单
    ORDER_TOUSER_START_SERVICE(WechatPagePathEnum.ORDER_LIST_PAGE, "【订单状态：陪玩中】备注：陪玩师开始陪玩服务啦，快快上号开始吧。"), //开始服务
    ORDER_TOUSER_CHECK(WechatPagePathEnum.ORDER_LIST_PAGE, "【订单状态：等待验收】 备注：陪玩师向发起了完成服务申请，快去确认吧。"), //提交验收
    ORDER_TOUSER_APPEAL(WechatPagePathEnum.ORDER_LIST_PAGE, "【订单状态：申请仲裁】备注：对于您的协商，陪玩师申请客服进行仲裁，快去提交凭证吧（2小时内提交）。"), //用户发起仲裁
    ORDER_TOUSER_APPEAL_USER_WIN(WechatPagePathEnum.ORDER_LIST_PAGE, "【订单状态：仲裁完成:老板胜诉】 备注：您的订单仲裁结果为:老板胜诉，将会为你全额退款。"), //管理员仲裁老板胜
    ORDER_TOUSER_APPEAL_SERVICE_WIN(WechatPagePathEnum.ORDER_LIST_PAGE, "【订单状态：仲裁完成:陪玩师胜诉】 备注：您的订单仲裁结果为:陪玩师胜诉，订单金额将会支付给陪玩师。"), //管理员仲裁陪玩师胜
    ORDER_TOUSER_CONSULT_REJECT(WechatPagePathEnum.ORDER_LIST_PAGE, "【订单状态：协商拒绝】 备注：对于您的协商，陪玩师进行了拒绝，快去处理吧，超过2小时未处理，将默认取消协商。"), //拒绝协商
    ORDER_TOUSER_CONSULT_AGREE(WechatPagePathEnum.ORDER_LIST_PAGE, "【订单状态：同意协商】 备注：对于您的协商，陪玩师进行了同意，金额将退回到您的微信账号。"), //同意协商

    ORDER_TOSERVICE_PAY(WechatPagePathEnum.ORDER_LIST_PAGE, "有老板向您下单啦，快去查看吧，超过24小时未接单，订单将自动取消。"), //下单
    ORDER_TOSERVICE_REMIND_RECEIVE(WechatPagePathEnum.ORDER_LIST_PAGE, "老板提醒您接单，快去处理吧，超过24小时未接单，订单将自动取消。"), //提醒接单
    ORDER_TOSERVICE_REMIND_START_SERVICE(WechatPagePathEnum.ORDER_LIST_PAGE, "老板提醒您开始服务，快去处理吧，超过指定时间未开始，订单将自动取消。"), //提醒开始
    ORDER_TOSERVICE_AFFIRM_SERVER(WechatPagePathEnum.ORDER_LIST_PAGE, "老板已确认服务完成，收益到账了哦。"), //确认服务完成
    ORDER_TOSERVICE_ORDER_CANCEL(WechatPagePathEnum.ORDER_LIST_PAGE, "很遗憾，老板取消了订单，要再接再厉哦。"), //确认服务完成
    ORDER_TOSERVICE_CONSULT(WechatPagePathEnum.ORDER_LIST_PAGE, "老板发起协商，申请退款，快去处理吧，超过24小时未处理，将默认同意协商。"), //确认服务完成
    ORDER_TOSERVICE_APPEAL(WechatPagePathEnum.ORDER_LIST_PAGE, "老板对您的回馈不满意，申请了客服仲裁，快去提交凭证吧（2小时内提交）。"), //确认服务完成
    ORDER_TOSERVICE_APPEAL_USER_WIN(WechatPagePathEnum.ORDER_LIST_PAGE, "您的订单仲裁结果为:老板胜诉，订单金额将会全额退款给老板。"), //确认服务完成
    ORDER_TOSERVICE_APPEAL_SERVICE_WIN(WechatPagePathEnum.ORDER_LIST_PAGE, "您的订单仲裁结果为:陪玩师胜诉，订单金额将会支付给您。"), //确认服务完成
    ORDER_TOSERVICE_CONSULT_CANCEL(WechatPagePathEnum.ORDER_LIST_PAGE, "老板取消了协商，服务照常进行哦。"), //确认服务完成
    ORDER_SYSTEM_APPEAL_NEGOTIATE(WechatPagePathEnum.ORDER_LIST_PAGE, "您的订单仲裁结果为:协商处理。客服回复:{}"), //确认服务完成

    IM_MSG_PUSH(WechatPagePathEnum.IM_MSG, "{}:{}"),

    MARKET_ORDER_PUSH(WechatPagePathEnum.POINT_ORDER_PAGE, "有新的{}订单，快来看看吧"), //接单路径

    POINT_TOSERVICE_ORDER_RECEIVING(WechatPagePathEnum.ORDER_LIST_PAGE, "系统为您派单了,快去处理吧,若10分钟未处理则自动取消订单并对您进行惩罚。"),
    POINT_TOSE_ORDER_RECEIVING(WechatPagePathEnum.ORDER_LIST_PAGE, "若陪玩师10分钟内未进行处理，订单取消。");


    //跳转页面
    private WechatPagePathEnum page;
    
    //内容
    private String content;

    }
