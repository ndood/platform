package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class SystemException  extends RuntimeException{

    private ExceptionCode exceptionCode;

    private Integer code;
    private String message;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode{
        NO_FORM_TOKEN_ERROR(503, "页面超时,需要重新刷新页面后再尝试!");
        private int code;
        private String msg;
    }

    public SystemException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }
}