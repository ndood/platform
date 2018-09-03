package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 虚拟商品种类枚举类
 *
 * @author Gong ZeChun
 * @date 2018/8/30 15:09
 */
@Getter
@AllArgsConstructor
public enum VirtualProductTypeEnum implements TypeEnum<Integer> {
    VIRTUAL_GIFT(1, "礼物"),
    PERSONAL_PICS(2, "私照图片组"),
    IM_PROTECTED_PICS(3, "IM解锁图片组"),
    IM_PROTECTED_VOICE(4, "IM解锁语音"),
    LOGIN_RECEIVE_BONUS(5, "登录用户奖励10钻石");

    private Integer type;
    private String msg;
}
