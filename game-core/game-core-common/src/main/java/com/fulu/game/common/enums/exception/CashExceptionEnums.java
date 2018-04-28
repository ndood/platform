package com.fulu.game.common.enums.exception;

import com.fulu.game.common.exception.IExceptionCode;

public enum CashExceptionEnums implements IExceptionCode {
    CASH_NEGATIVE_EXCEPTION (-1,"金额小于0"),
    CASH_EXCEED_EXCEPTION  (100,"提款金额超出余额");
    private int code;
    private String message;

    private CashExceptionEnums(int code, String message) {
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
