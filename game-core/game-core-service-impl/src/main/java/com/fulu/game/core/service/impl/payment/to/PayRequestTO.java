package com.fulu.game.core.service.impl.payment.to;

import lombok.Data;

@Data
public class PayRequestTO {

    boolean directPay;

    Object requestParameter;

    public PayRequestTO(boolean directPay){
        this.directPay = directPay;
    }

}
