package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WechatTemplateIdEnum {


    PLAY_LEAVE_MSG("yD7JulFzNv7ZNInswmn6_hdgwlf68qRL0fwLUNq98Vc", "留言消息模板"),
    POINT_LEAVE_MSG("q4vzN44BKXw-ZDLH-g7qsPgFb6UEj0enkutCwNfAuh0", "留言消息模板"),
    PLAY_SERVICE_PROCESS_NOTICE("tXFoUhfqrMRADhrF4Sp1yaiUwpw6H6XwBc_xf1LJzo8", "服务进度通知模板"),
    POINT_SERVICE_PROCESS_NOTICE("pO3Wjr5bbtiublql1uV4gfdEypWO7g3z5_zFdLa8-Wg", "服务进度通知模板");

    private String templateId;
    private String msg;
}
