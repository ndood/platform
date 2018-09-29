package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短信文本内容枚举类
 *
 * @author Gong ZeChun
 * @date 2018/9/28 19:12
 */
@Getter
@AllArgsConstructor
public enum SMSContentEnum implements TypeEnum<Integer> {
    SERVER_CANCEL_ORDER(1, "陪玩师太忙了，为了不耽误您的服务体验，拒绝了订单，望老板大人理解。(分期乐来源订单)"),
    START_SERVER_ORDER(2, "陪玩师开始陪玩服务啦，快快上号开始吧。(分期乐来源订单)"),
    USER_APPEAL_ORDER(3, "对于您的协商，陪玩师申请客服进行仲裁，请在2小时内提交凭证（分期乐来源订单）"),
    CONSULT_REJECT(4, "对于您的协商，陪玩师进行了拒绝，快去处理吧，超过2小时未处理，将默认取消协商。(分期乐来源订单)"),
    CONSULT_APPEAL(5, "对于您的协商，陪玩师进行了同意，服务金额将原路返回。(分期乐来源订单)"),
    NOT_ACCEPT_ORDER(6, "陪玩师太忙了，没有点击接单，望老板大人理解。(分期乐来源订单)"),
    NOT_START_ORDER(7, "陪玩师太忙了，没有点击开始，望老板大人理解。(分期乐来源订单)");

    private Integer type;
    private String msg;
}
