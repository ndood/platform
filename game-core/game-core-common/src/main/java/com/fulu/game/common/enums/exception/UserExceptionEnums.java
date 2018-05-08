package com.fulu.game.common.enums.exception;

import com.fulu.game.common.exception.IExceptionCode;

public enum UserExceptionEnums implements IExceptionCode {

    USER_NOT_EXIST_EXCEPTION (50001,"用户不存在"),
    IllEGAL_MOBILE_EXCEPTION (50002,"手机号为空或不合法");
    private int code;
    private String message;

    private UserExceptionEnums(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
