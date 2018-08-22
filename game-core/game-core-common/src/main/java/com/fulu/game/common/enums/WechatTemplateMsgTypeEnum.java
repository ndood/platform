package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 微信模板消息类型枚举类
 *
 * @author Gong ZeChun
 * @date 2018/8/22 15:59
 */
@Getter
@AllArgsConstructor
public enum WechatTemplateMsgTypeEnum implements TypeEnum<Integer> {

    LEAVE_MESSAGE(1, "留言通知"),
    SERVICE_PROCESS_NOTICE(2, "服务进度通知");

    private Integer type;
    private String msg;
}
