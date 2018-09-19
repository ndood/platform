package com.fulu.game.core.entity.payment.model;

import com.fulu.game.common.enums.PayBusinessEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundModel {



    /**
     * 支付方式
     */
    private Integer payment;

    /**
     * 支付业务
     */
    private PayBusinessEnum payBusinessEnum;


    //订单号
    String orderNo;
    //订单总金额
    BigDecimal totalMoney;
    //退款金额
    BigDecimal refundMoney;


}
