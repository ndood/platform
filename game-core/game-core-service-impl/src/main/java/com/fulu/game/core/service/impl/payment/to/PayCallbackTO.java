package com.fulu.game.core.service.impl.payment.to;

import lombok.Data;

@Data
public class PayCallbackTO {




    //是否支付成功
    private boolean isSuccess;

    //支付订单号
    private String orderNO;

    //支付金额
    private String payMoney;


}
