package com.fulu.game.thirdparty.fenqile.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class FenqileOrderRequest {

    //商户编号
    private String skuId;
    //分期乐用户id
    private String thirdUid;
    //订单金额
    private BigDecimal amount;
    //第三方订单号
    private String thirdOrderId;
    //下单时间
    private String createTime;
    //商品描述
    private String subject;

    private String clientIp;

    private String attach;

    private String body;

    private String timeStart;

    private String timeExpire;
}
