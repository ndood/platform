package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class VirtualProductException extends BizException {
    private ExceptionCode exceptionCode;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode {
        BALANCE_NOT_ENOUGH_EXCEPTION(11001, "钻石不足！"),
        NOT_EXIST(11002, "虚拟商品不存在！");
        private int code;
        private String msg;
    }

    public VirtualProductException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }
}
