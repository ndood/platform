package com.fulu.game.play.controller.exception;

import com.fulu.game.common.exception.IExceptionCode;

public class ParamsException extends RuntimeException{
    private int code;
    private String message;

    public ParamsException(IExceptionCode iExceptionCode) {
        super();
        this.code = iExceptionCode.getCode();
        this.message = iExceptionCode.getMessage();
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
