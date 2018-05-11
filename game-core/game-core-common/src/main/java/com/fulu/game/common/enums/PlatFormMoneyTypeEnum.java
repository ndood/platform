package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlatFormMoneyTypeEnum {


    SHARE_PROFIT("订单分润:"),
    ORDER_PAY("订单支付:"),
    ORDER_REFUND("订单退款:"),
    ORDER_NEGOTIATE("订单协商:");

    String type;
}
