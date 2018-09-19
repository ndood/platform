package com.fulu.game.core.entity.payment.model;

import com.fulu.game.common.enums.PayBusinessEnum;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class RefundModel {


    //订单号
    String orderNo;
    //订单总金额
    BigDecimal totalMoney;
    //退款金额
    BigDecimal refundMoney;
    /**
     * 支付方式
     */
    private Integer payment;
    private Integer platform;
    /**
     * 支付业务
     */
    private PayBusinessEnum payBusinessEnum;

    private RefundModel(Builder builder) {
        payment = builder.payment;
        platform = builder.platform;
        payBusinessEnum = builder.payBusinessEnum;
        orderNo = builder.orderNo;
        totalMoney = builder.totalMoney;
        refundMoney = builder.refundMoney;
    }

    public static Builder newBuilder(Integer payment, PayBusinessEnum payBusinessEnum) {
        return new Builder(payment, payBusinessEnum);
    }


    public static final class Builder {
        private Integer payment;
        private Integer platform;
        private PayBusinessEnum payBusinessEnum;
        private String orderNo;
        private BigDecimal totalMoney;
        private BigDecimal refundMoney;

        private Builder(Integer payment, PayBusinessEnum payBusinessEnum) {
            this.payment = payment;
            this.payBusinessEnum = payBusinessEnum;
        }


        public Builder platform(Integer val) {
            platform = val;
            return this;
        }


        public Builder orderNo(String val) {
            orderNo = val;
            return this;
        }

        public Builder totalMoney(BigDecimal val) {
            totalMoney = val;
            return this;
        }

        public Builder refundMoney(BigDecimal val) {
            refundMoney = val;
            return this;
        }

        public RefundModel build() {
            if(orderNo==null||totalMoney==null||refundMoney==null){
                throw new IllegalArgumentException("orderNo,totalMoney,refundMoney 字段不能空");
            }
            return new RefundModel(this);
        }
    }
}