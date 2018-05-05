package com.fulu.game.common.enums.exception;

import com.fulu.game.common.exception.IExceptionCode;

public enum OrderExceptionEnums implements IExceptionCode {

    ORDER_NOT_EXIST_EXCEPTION (-1,"订单不存在");
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
