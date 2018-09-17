package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 余额支付异常类
 *
 * @author Gong ZeChun
 * @date 2018/9/13 19:42
 */
@Getter
public class PayException extends BizException {
    private ExceptionCode exceptionCode;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode {
        BALANCE_NOT_ENOUGH_EXCEPTION(90000, "余额不足！"),
        PAY_FAIL(90001, "支付失败！"),
        PAYMENT_UN_MATCH(90002, "支付方式不匹配！"),
        WECHAT_CREATE_PAY_FAIL(90003, "微信创建支付订单失败！");

        private int code;
        private String msg;
    }

    public PayException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }



}
