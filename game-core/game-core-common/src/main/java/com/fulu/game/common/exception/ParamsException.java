package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ParamsException extends BizException{
    private ExceptionCode exceptionCode;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode{
        PARAM_NULL_EXCEPTION (10001,"必要参数为空");
        private int code;
        private String msg;
    }

    public ParamsException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }
}
