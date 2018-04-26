package com.fulu.game.common.enums.exception;

import com.fulu.game.common.exception.IExceptionCode;

public enum UserExceptionEnums implements IExceptionCode {

    USER_NOT_EXIST_EXCEPTION ("-1","用户不存在");
    private String Code;
    private String Message;

    private UserExceptionEnums(String Code, String Message) {
        this.Code = Code;
        this.Message = Message;
    }
    public String getCode() {
        return Code;
    }

    public String getMessage() {
        return Message;
    }
}
