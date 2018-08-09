package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WechatTemplateIdEnum{


    PLAY_LEAVE_MSG("yD7JulFzNv7ZNInswmn6_hdgwlf68qRL0fwLUNq98Vc","留言消息模板"),
    POINT_LEAVE_MSG("q4vzN44BKXw-ZDLH-g7qsPgFb6UEj0enkutCwNfAuh0","留言消息模板");

    private String templateId;
    private String msg;
}
