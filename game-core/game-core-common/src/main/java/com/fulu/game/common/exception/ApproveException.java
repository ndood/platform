package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ApproveException extends BizException {

    private ExceptionCode exceptionCode;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode {
        APPROVE_DUPLICATE(40001, "已为该陪玩师认可过一次技能"),
        APPROVE_FREEZE(40002, "该技能已被冻结，无法认可");
        private int code;
        private String msg;
    }

    public ApproveException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }
}
