package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WechatTemplateMsgEnum {

    TECH_AUTH_AUDIT_ING(WechatEcoEnum.PLAY.getType(),PagePathEnum.SERVICE_USER_VERIFY.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "您的好友{}成功帮您认证,还剩{}位好友认证即可成功通过审核."), //好友认证
    TECH_AUTH_AUDIT_SUCCESS(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_RECEIVING_SETTING.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "恭喜您,成功通过审核,快快前往接单啦。"),
    TECH_AUTH_AUDIT_FAIL(WechatEcoEnum.PLAY.getType(),PagePathEnum.SERVICE_USER_VERIFY.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "审核未通过:{},请前往重新提交审核."),
    GRANT_COUPON(WechatEcoEnum.PLAY.getType(),PagePathEnum.COUPON_LIST.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "恭喜你,获得一张{}元优惠券"),
    USER_AUTH_INFO_PASS(WechatEcoEnum.PLAY.getType(),PagePathEnum.SERVICE_USER_VERIFY.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "您提交申请的陪玩师个人资料已经审核通过，快去玩耍接单吧!"),
    USER_AUTH_INFO_REJECT(WechatEcoEnum.PLAY.getType(),PagePathEnum.SERVICE_USER_VERIFY.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "您提交的陪玩师个人资料申请被驳回，理由:{}"),

    ORDER_TOUSER_REJECT_RECEIVE(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "【订单状态：{}】备注：陪玩师太忙了，为了不耽误您的游戏体验，拒绝了订单，望老板大人理解。"),//拒绝接单
    ORDER_TOUSER_AFFIRM_RECEIVE(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "【订单状态：{}】备注：陪玩师已经接单啦，一起谈谈什么时候开始呢？超过24小时未开始，订单将自动取消。"),//已经接单
    ORDER_TOUSER_START_SERVICE(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "【订单状态：{}】备注：陪玩师开始陪玩服务啦，快快上号开始吧。"), //开始服务
    ORDER_TOUSER_CHECK(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "【订单状态：{}】 备注：陪玩师向发起了完成服务申请，快去确认吧。"), //提交验收
    ORDER_TOUSER_APPEAL(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "【订单状态：{}】备注：对于您的协商，陪玩师申请客服进行仲裁，快去提交凭证吧（2小时内提交）。"), //仲裁
    ORDER_TOUSER_APPEAL_USER_WIN(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "【订单状态：{}】 备注：您的订单仲裁结果为:老板胜诉，将会为你全额退款。"), //仲裁
    ORDER_TOUSER_APPEAL_SERVICE_WIN(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "【订单状态：{}】 备注：您的订单仲裁结果为:陪玩师胜诉，订单金额将会支付给陪玩师。"), //仲裁
    ORDER_TOUSER_CONSULT_REJECT(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "【订单状态：{}】 备注：对于您的协商，陪玩师进行了拒绝，快去处理吧，超过2小时未处理，将默认取消协商。"), //拒绝协商
    ORDER_TOUSER_CONSULT_AGREE(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "【订单状态：{}】 备注：对于您的协商，陪玩师进行了同意，金额将退回到您的微信账号。"), //同意协商

    ORDER_TOSERVICE_PAY(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "有老板向您下单啦，快去查看吧，超过24小时未接单，订单将自动取消。"), //下单
    ORDER_TOSERVICE_REMIND_RECEIVE(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "老板提醒您接单，快去处理吧，超过24小时未接单，订单将自动取消。"), //提醒接单
    ORDER_TOSERVICE_REMIND_START_SERVICE(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "老板提醒您接单，快去处理吧，超过24小时未接单，订单将自动取消。"), //提醒开始
    ORDER_TOSERVICE_AFFIRM_SERVER(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "老板已确认服务完成，收益到账了哦。"), //确认服务完成
    ORDER_TOSERVICE_ORDER_CANCEL(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "很遗憾，老板取消了订单，要再接再厉哦。"), //确认服务完成
    ORDER_TOSERVICE_CONSULT(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "老板发起协商，申请退款，快去处理吧，超过24小时未处理，将默认同意协商。"), //确认服务完成
    ORDER_TOSERVICE_APPEAL(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "老板对您的回馈不满意，申请了客服仲裁，快去提交凭证吧（2小时内提交）。"), //确认服务完成
    ORDER_TOSERVICE_APPEAL_USER_WIN(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "您的订单仲裁结果为:老板胜诉，订单金额将会全额退款给老板。"), //确认服务完成
    ORDER_TOSERVICE_APPEAL_SERVICE_WIN(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "您的订单仲裁结果为:陪玩师胜诉，订单金额将会支付给您。"), //确认服务完成
    ORDER_TOSERVICE_CONSULT_CANCEL(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "老板取消了协商，服务照常进行哦。"), //确认服务完成
    ORDER_SYSTEM_APPEAL_NEGOTIATE(WechatEcoEnum.PLAY.getType(),PagePathEnum.ORDER_LIST_PAGE.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "您的订单仲裁结果为:协商处理。客服回复:{}"), //确认服务完成

    IM_MSG_PUSH(WechatEcoEnum.PLAY.getType(),PagePathEnum.IM_MSG.getPagePath(), WechatTemplateEnum.PLAY_LEAVE_MSG, "{}:{}"),

    MARKET_ORDER_PUSH(WechatEcoEnum.POINT.getType(),"", WechatTemplateEnum.PLAY_LEAVE_MSG, "有新的{}订单，快来看看吧"), //接单路径

    POINT_TOSERVICE_ORDER_RECEIVING(WechatEcoEnum.POINT.getType(),"",WechatTemplateEnum.POINT_LEAVE_MSG,"系统为您派单了,快去处理吧,若10分钟未处理则自动取消订单并对您进行惩罚。"),//todo 上分订单路径
    POINT_TOSE_ORDER_RECEIVING(WechatEcoEnum.POINT.getType(),"",WechatTemplateEnum.POINT_LEAVE_MSG,"若陪玩师10分钟内未进行处理，订单取消。"),//todo 上分订单路径

    POINT_REPLACE_TEMPLATE(WechatEcoEnum.POINT.getType(),"",WechatTemplateEnum.POINT_LEAVE_MSG,"");

    private int platform;
    //跳转页面
    private String page;
    //模板代码
    private WechatTemplateEnum templateId;
    //内容
    private String content;

    public WechatTemplateMsgEnum choice(Integer ecoType){
        if(WechatEcoEnum.POINT.getType().equals(ecoType)){
            POINT_REPLACE_TEMPLATE.page = PagePathEnum.findSfPatgByPagePath(this.page);
            POINT_REPLACE_TEMPLATE.content = PagePathEnum.findSfPatgByPagePath(this.content);
            return POINT_REPLACE_TEMPLATE;
        }else{
            return this;
        }

    }
}
