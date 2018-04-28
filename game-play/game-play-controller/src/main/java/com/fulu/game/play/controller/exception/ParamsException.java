package com.fulu.game.play.controller.exception;

import com.fulu.game.common.exception.IExceptionCode;

public class ParamsException extends RuntimeException{
    private int Code;
    private String Message;

    public ParamsException(IExceptionCode iExceptionCode) {
        super();
        this.Code = iExceptionCode.getCode();
        this.Message = iExceptionCode.getMessage();
    }

    public int getCode() {
        return Code;
    }

    @Override
    public String getMessage() {
        return Message;
    }
}
