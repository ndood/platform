package com.fulu.game.common.exception;

/**
 * 提款打款业务自定义异常
 * yanbiao
 * 2018.4.25
 */
public class CashException extends RuntimeException {
    private IExceptionCode iExceptionCode;

    private String code;
    private String message;

    public CashException(IExceptionCode iExceptionCode) {
        super();
        this.iExceptionCode = iExceptionCode;
        this.code = iExceptionCode.getCode();
        this.message = iExceptionCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
