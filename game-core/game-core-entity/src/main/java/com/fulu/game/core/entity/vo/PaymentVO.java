package com.fulu.game.core.entity.vo;


import com.fulu.game.common.enums.PayBusinessEnum;
import com.fulu.game.common.enums.PaymentEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class PaymentVO {

    /**
     * 支付业务
     */
    private PayBusinessEnum payBusinessEnum;

    /**
     * 支付方式
     */
    private PaymentEnum paymentEnum;


    private Order order;

    private User user;

    private String userIp;
}
