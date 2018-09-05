package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 虚拟货币和魅力值流水表-备注枚举类
 *
 * @author Gong ZeChun
 * @date 2018/9/5 15:25
 */
@Getter
@AllArgsConstructor
public enum VirtualDetailsRemarkEnum implements TypeEnum<Integer> {
    GIFT_COST(1, "礼物消费"),
    UNLOCK_PERSONAL_PICS(2, "解锁私密照"),
    UNLOCK_PICS(3, "解锁图片"),
    UNLOCK_VOICE(4, "解锁语音"),
    LOGIN_BOUNS(5, "登录奖励"),
    GIFT_RECEIVE(6, "接收礼物"),
    CHARGE(7, "钻石充值");

    private Integer type;
    private String msg;
}
