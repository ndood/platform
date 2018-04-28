package com.fulu.game.common.exception;

public class UserException extends RuntimeException {

    private int code;
    private String message;

    public UserException(IExceptionCode iExceptionCode) {
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
