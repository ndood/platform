package com.fulu.game.core.entity.vo;

import com.fulu.game.common.enums.PayBusinessEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualPayOrder;

public class PaymentVO {

    private PayBusinessEnum payBusinessEnum;

    private Order order;

    private VirtualPayOrder virtualPayOrder;

    private User user;

    private String receiptData;

}
