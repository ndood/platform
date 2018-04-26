package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  OrderStatusEnum{

    NON_PAYMENT(100,"待付款"),
    WAIT_SERVICE(200,"等待陪玩"),
    SERVICING(210,"陪玩中"),
    SERVER_CANCEL(400,"取消订单"),//陪玩师取消订单
    USER_CANCEL(401,"取消订单");//用户取消订单

    private Integer status;
    private String msg;






}
