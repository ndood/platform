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
        URL_PATH_ERROR(7002, "url路径错误!");
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
