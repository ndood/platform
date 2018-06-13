package com.fulu.game.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ProductException  extends BizException{



    private ExceptionCode exceptionCode;

    @AllArgsConstructor
    @Getter
    public enum ExceptionCode{
        PRODUCT_NOT_EXIST(42001, "陪玩师正在调整该商品,请稍后再来下单!"),
        PRODUCT_REVIEW_ING(42002, "该技能未审核通过,不允许操作!");
        private int code;
        private String msg;
    }

    public ProductException(ExceptionCode exceptionCode) {
        super();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMsg();
        this.exceptionCode = exceptionCode;
    }
}
