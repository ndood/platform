package com.fulu.game.core.entity.payment.model;

import com.fulu.game.common.enums.PayBusinessEnum;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualPayOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PayRequestModel {

    //支付业务
    private PayBusinessEnum payBusinessEnum;


    //业务订单
    private Order order;
    //虚拟支付订单
    private VirtualPayOrder virtualPayOrder;
    //用户对象
    private User user;
    //苹果支付串
    private String receiptData;




    private PayRequestModel(Builder builder) {
        payBusinessEnum = builder.payBusinessEnum;
        order = builder.order;
        virtualPayOrder = builder.virtualPayOrder;
        user = builder.user;
        receiptData = builder.receiptData;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private PayBusinessEnum payBusinessEnum;
        private Order order;
        private VirtualPayOrder virtualPayOrder;
        private User user;
        private String receiptData;

        private Builder() {
        }


        public Builder order(Order val) {
            order = val;
            return this;
        }

        public Builder virtualPayOrder(VirtualPayOrder val) {
            virtualPayOrder = val;
            return this;
        }

        public Builder user(User val) {
            user = val;
            return this;
        }

        public Builder receiptData(String val) {
            receiptData = val;
            return this;
        }

        public PayRequestModel build() {
            if(order==null&&virtualPayOrder==null){
                throw new IllegalArgumentException("必须构建一个order和virtualPayOrder参数!");
            }
            if(order==null){
                payBusinessEnum = PayBusinessEnum.VIRTUAL_PRODUCT;
            }else{
                payBusinessEnum = PayBusinessEnum.ORDER;
            }
            return new PayRequestModel(this);
        }
    }
}
