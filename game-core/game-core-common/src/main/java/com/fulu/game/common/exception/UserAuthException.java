package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserAuthException extends BizException{



    private ExceptionCode exceptionCode;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode{
        EXIST_USER_AUTH(40001, "个人信息已认证过,不能重复认证!"),
        SERVICE_USER_REJECT(40002, "您当前的陪玩介绍已被管理员驳回,无法开启接单,请查看驳回原因后按规定修改个人信息，重新开始接单。"),
        SERVICE_USER_FREEZE(40003, "您当前的陪玩身份已被冻结,如有异议请联系管理员。");

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
