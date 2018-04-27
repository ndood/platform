package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SMSTemplateEnum implements TypeEnum<String>{

    VERIFICATION_CODE("216457","短信验证码"), //245842
    ORDER_RECEIVING_REMIND("245844","接单提醒");

    private String type;
    private String msg;


}
