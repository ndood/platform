package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class CouponException extends BizException {

    private int code;
    private String message;
    private ExceptionCode exceptionCode;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode{
        REDEEMCODE_ERROR(50001, "优惠券兑换码错误！"),
        BROUGHT_OUT(50002, "该优惠券已领完,不能再领了！"),
        ALREADY_RECEIVE(50003, "您已领过该优惠券,不能再领了！"),
        NEWUSER_RECEIVE(50004, "该优惠券只能新人用户领取！"),
        OVERDUE(50005, "该优惠券已过期,不能兑换！");
        private int code;
        private String msg;
    }

    public CouponException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }



}
