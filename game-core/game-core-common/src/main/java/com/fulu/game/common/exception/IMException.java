package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class IMException extends BizException{


    private ExceptionCode exceptionCode;

    private Integer code;
    private String message;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode{
        IM_REGISTER_FAIL(8001, "聊天暂时功能不可用,请联系客服!");
        private int code;
        private String msg;
    }

    public IMException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }
}
