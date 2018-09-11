package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
public class LoginException extends RuntimeException {

    private ExceptionCode exceptionCode;

    private Integer code;
    private String message;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode{
        VERIFY_CODE_ERROR(3001, "验证码错误!"),
        WX_AUTH_ERROR(3002, "微信授权失效!");
        private int code;
        private String msg;
    }

    public LoginException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }
}
