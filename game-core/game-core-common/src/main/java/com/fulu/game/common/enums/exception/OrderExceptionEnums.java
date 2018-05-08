package com.fulu.game.common.enums.exception;

import com.fulu.game.common.exception.IExceptionCode;

public enum OrderExceptionEnums implements IExceptionCode {

    ORDER_NOT_EXIST_EXCEPTION (5001, "订单不存在"),
    ORDER_STATUS_EXCEPTION (5002,"订单状态不匹配");
    private int code;
    private String message;

    private OrderExceptionEnums(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
