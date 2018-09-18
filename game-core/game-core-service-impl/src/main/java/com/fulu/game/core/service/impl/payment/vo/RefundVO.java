package com.fulu.game.core.service.impl.payment.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundVO {

    //订单号
    String orderNo;
    //订单总金额
    BigDecimal totalMoney;
    //退款金额
    BigDecimal refundMoney;


}
