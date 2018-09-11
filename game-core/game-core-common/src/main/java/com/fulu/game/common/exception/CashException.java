package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 提款打款业务自定义异常
 * yanbiao
 * 2018.4.25
 */
@Getter
public class CashException extends BizException {
    private ExceptionCode exceptionCode;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode {
        CASH_NEGATIVE_EXCEPTION(30001, "金额小于0"),
        CASH_EXCEED_EXCEPTION(30002, "提款金额超出余额"),
        CASH_CUT_EXCEPTION(30003, "扣款金额超出余额"),
        CASH_ORDER_FAIL_EXCEPTION(30004, "下单失败，请联系客服"),
        CASH_REMIT_FAIL_EXCEPTION(30005, "打款失败，请联系管理员"),
        CHARM_WITHDRAW_FAIL_EXCEPTION(30006, "提现魅力值超出超出剩余量");
        private int code;
        private String msg;
    }

    public CashException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }
}
