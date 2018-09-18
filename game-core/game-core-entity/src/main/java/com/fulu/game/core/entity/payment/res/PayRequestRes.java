package com.fulu.game.core.entity.payment.res;

import lombok.Data;

@Data
public class PayRequestRes {

    boolean directPay;

    Object requestParameter;

    public PayRequestRes(boolean directPay){
        this.directPay = directPay;
    }

}
