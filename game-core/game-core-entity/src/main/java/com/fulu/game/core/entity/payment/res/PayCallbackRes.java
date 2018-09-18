package com.fulu.game.core.entity.payment.res;

import lombok.Data;

@Data
public class PayCallbackRes {


    //是否支付成功
    private boolean isSuccess;

    //支付订单号
    private String orderNO;

    //支付金额
    private String payMoney;


}
