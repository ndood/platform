package com.fulu.game.common.exception;

public class UserException extends RuntimeException {

    private String Code;
    private String Message;

    public UserException(IExceptionCode iExceptionCode) {
        super();
        this.Code = iExceptionCode.getCode();
        this.Message = iExceptionCode.getMessage();
    }

    public String getCode() {
        return Code;
    }

    @Override
    public String getMessage() {
        return Message;
    }
}
