package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum  DetailsEnum {

    ORDER_PAY("订单支付"),
    ORDER_SERVER_CANCEL("陪玩师取消订单退款"),
    ORDER_USER_CANCEL("用户取消订单退款");

    private String msg;

}
