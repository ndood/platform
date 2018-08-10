package com.fulu.game.thirdparty.fenqile.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FenqileOrderNotice {

    private String thirdOrderId;

    private String orderId;

    private Integer merchSaleState;


    private String sign;

    private BigDecimal amount;

    private String subject;

    private String body;

    private String attach;


}
