package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SMSTemplateEnum implements TypeEnum<String> {
    VERIFICATION_CODE("245842", "短信验证码"),
    ORDER_RECEIVING_REMIND("247920", "接单提醒"),
    SENDLEAVE_INFORM("260498", "开黑留言"),
    SENDLEAVE_INFORM_NO_URL("344669", "开黑留言不带url"),
    SMS_REMIND("348655", "开黑陪玩消息提醒");

    private String type;
    private String msg;
}
