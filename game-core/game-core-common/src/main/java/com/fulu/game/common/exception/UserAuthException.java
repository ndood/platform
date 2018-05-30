package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserAuthException extends BizException{



    private ExceptionCode exceptionCode;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode{
        EXIST_USER_AUTH(21001, "个人信息已认证过,不能重复认证!"),
        SERVICE_USER_AUTH(21001, "个人信息已认证过,不能重复认证!");

        private int code;
        private String msg;
    }

    public UserAuthException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }
}
