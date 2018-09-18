package com.fulu.game.core.entity.payment.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundModel {

    //订单号
    String orderNo;
    //订单总金额
    BigDecimal totalMoney;
    //退款金额
    BigDecimal refundMoney;


}
