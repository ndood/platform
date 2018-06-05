package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用异常或其他异常
 * yanbiao
 * 2018.4.25
 */
@Getter
public class CommonException extends BizException {
    private ExceptionCode exceptionCode;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode {
        RECORD_NOT_EXSISTS(90001, "记录不存在");
        private int code;
        private String msg;
    }

    public CommonException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }
}
