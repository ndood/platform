package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserAuthException extends BizException{



    private ExceptionCode exceptionCode;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode{
        NOT_EXIST_USER_AUTH(40000, "未认证个人信息,不能操作!"),
        EXIST_USER_AUTH(40001, "个人信息已认证过,不能重复认证!"),
        SERVICE_USER_AUTHING(40004, "您的个人信息还在审核中，审核通过后则可设置接单方式。"),
        SERVICE_USER_REJECT(40002, "您的个人信息暂未认证通过，通过后则可设置接单方式。"),
        SERVICE_USER_FREEZE(40003, "您当前的陪玩身份已被冻结,如有异议请联系管理员。"),
        SERVICE_USER_FREEZE_ADMIN(40003, "当前陪玩师身份为冻结状态,不能操作!"),
        USER_TECH_NO_AUTHENTICATION(41001, "该技能未认证通过不能操作接单方式。"),
        USER_TECH_AUTHENTICATION_ING(41002, "该技能在认证中不能操作接单方式。"),
        USER_TECH_FREEZE(41003, "该技能被冻结,不能继续操作。");

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
