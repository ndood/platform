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
        WECHAT_CREATE_PAY_FAIL(90003, "微信创建支付订单失败！"),
        CHARGE_VALUE_ERROR(90004, "充值金额不正确"),
        APPLE_STORE_CHECK_ERROR(90005, "苹果内购信息验证失败!"),
        REPETITION_PAY(90006, "不能重复支付!"),
        CALLBACK_FAIL(90007, "支付回调解析失败!"),
        THIRD_REFUND_FAIL(90008, "调用第三方退款失败!");

        private int code;
        private String msg;
    }

    public PayException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }
    public PayException(ExceptionCode exceptionCode,String orderNo) {
        super();
        this.code = exceptionCode.getCode();
        this.message = "订单号["+orderNo+"];"+exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }



}
