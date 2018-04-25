package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  OrderStatusEnum{

    NON_PAYMENT(100,"待付款"),
    WAIT_SERVICE(200,"等待服务");

    private Integer status;
    private String msg;






}
