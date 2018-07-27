package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WechatTemplateEnum implements TypeEnum<String>{


    PLAY_LEAVE_MSG("yD7JulFzNv7ZNInswmn6_hdgwlf68qRL0fwLUNq98Vc","推送消息模板"),

    POINT_LEAVE_MSG("q4vzN44BKXw-ZDLH-g7qsPgFb6UEj0enkutCwNfAuh0","推送消息模板");

    private String type;
    private String msg;
}
