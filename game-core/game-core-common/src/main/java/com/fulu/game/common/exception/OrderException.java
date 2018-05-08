package com.fulu.game.common.exception;

/**
 * 订单异常处理
 */
public class OrderException extends RuntimeException{

    private int code;
    private String message;


    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public OrderException(String orderNo,String message) {
        String msg = "["+orderNo+"]"+message;
        this.message = msg;
    }
}
