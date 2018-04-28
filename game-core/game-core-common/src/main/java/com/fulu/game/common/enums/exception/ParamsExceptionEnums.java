package com.fulu.game.common.enums.exception;

import com.fulu.game.common.exception.IExceptionCode;

public enum ParamsExceptionEnums implements IExceptionCode{

    PARAM_NULL_EXCEPTION (-1,"必要参数为空");
    private int code;
    private String message;

    private ParamsExceptionEnums(int code, String message) {
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
