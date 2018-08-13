package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SMSTemplateEnum implements TypeEnum<String>{

    VERIFICATION_CODE("245842","短信验证码"),
    ORDER_RECEIVING_REMIND("247920","接单提醒"),
    SENDLEAVE_INFORM("314670","开黑留言");

    private String type;
    private String msg;


}
