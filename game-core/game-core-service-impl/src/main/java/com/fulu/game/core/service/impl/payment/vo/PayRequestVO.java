package com.fulu.game.core.service.impl.payment.vo;

import com.fulu.game.common.enums.PayBusinessEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualPayOrder;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class PayRequestVO {

    private PayBusinessEnum payBusinessEnum;

    private Order order;

    private VirtualPayOrder virtualPayOrder;

    private User user;

    private String receiptData;

}
