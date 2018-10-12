package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 迅雷兑换码异常类
 *
 * @author Gong ZeChun
 * @date 2018/10/12 11:20
 */
public class ThunderCodeException extends BizException {
    private ExceptionCode exceptionCode;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode {
        WELFARE_USED(16000, "用户已领取该奖励"),
        NO_WELFARE_LEFT(16001, "该奖励已被领完");
        private int code;
        private String msg;
    }

    public ThunderCodeException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }
}
