package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlatFormMoneyTypeEnum {


    ORDER_SHARE_PROFIT("订单分润:"),
    ORDER_PAY("订单支付:"),
    COUPON_DEDUCTION("优惠券抵扣:"),
    ORDER_REFUND("订单退款:"),
    ORDER_NEGOTIATE("订单协商:"),
    SMALLCHANGE("加零钱:"),
    VIRTUAL_ORDER_PAY("虚拟币充值订单支付:");

    String type;
}
