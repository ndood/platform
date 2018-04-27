package com.fulu.game.common.enums.exception;

import com.fulu.game.common.exception.IExceptionCode;

public enum ParamsExceptionEnums implements IExceptionCode{

    PARAM_NULL_EXCEPTION ("-1","必要参数为空");
    private String code;
    private String message;

    private ParamsExceptionEnums(String code, String message) {
        this.code = code;
        this.message = message;
    }
    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
