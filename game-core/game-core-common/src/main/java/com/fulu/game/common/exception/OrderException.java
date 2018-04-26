package com.fulu.game.common.exception;

/**
 * 订单异常处理
 */
public class OrderException extends RuntimeException{



    public OrderException(String orderNo,String message) {
        super("["+orderNo+"]"+message);
    }
}
