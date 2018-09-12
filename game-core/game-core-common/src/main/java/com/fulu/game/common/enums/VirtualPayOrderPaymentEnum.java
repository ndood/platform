package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 虚拟币和余额充值订单表--支付方式枚举类
 *
 * @author Gong ZeChun
 * @date 2018/9/10 14:52
 */
@Getter
@AllArgsConstructor
public enum VirtualPayOrderPaymentEnum implements TypeEnum<Integer> {
    WECHAT_PAY(1, "微信支付"),
    BALANCE_PAY(2, "余额支付");

    private Integer type;
    private String msg;
}
