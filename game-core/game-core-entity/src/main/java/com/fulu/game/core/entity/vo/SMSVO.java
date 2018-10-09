package com.fulu.game.core.entity.vo;

import com.fulu.game.common.enums.SMSTemplateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class SMSVO {

    private String mobile;

    private SMSTemplateEnum templateEnum;

    private String[] params;

    public SMSVO(String mobile, SMSTemplateEnum smsTemplateEnum, String[] params) {
        this.mobile = mobile;
        this.templateEnum = smsTemplateEnum;
        this.params = params;
    }
}
