package com.fulu.game.core.entity.payment.model;

import com.fulu.game.common.enums.PayBusinessEnum;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class PayCallbackModel {

    /**
     * 支付方式
     */
    private Integer payment;

    /**
     * 支付平台(假如是微信平台该参数必填)
     */
    private Integer platform;

    /**
     * 支付业务
     */
    private PayBusinessEnum payBusinessEnum;

    /**
     * 支付宝回调参数
     */
    private Map<String,String>  aliPayParameterMap;

    /**
     * 微信回调参数
     */
    private String wechatXmlResult;




    private PayCallbackModel(Builder builder) {
        setPayment(builder.payment);
        setPlatform(builder.platform);
        setPayBusinessEnum(builder.payBusinessEnum);
        setAliPayParameterMap(builder.aliPayParameterMap);
        setWechatXmlResult(builder.wechatXmlResult);
    }



    public static Builder newBuilder(Integer payment,PayBusinessEnum payBusinessEnum) {
        return new Builder(payment,payBusinessEnum);
    }


    public static final class Builder {
        private Integer payment;
        private Integer platform;
        private PayBusinessEnum payBusinessEnum;
        private Map<String, String> aliPayParameterMap;
        private String wechatXmlResult;

        private Builder(Integer payment,PayBusinessEnum payBusinessEnum) {
            this.payment = payment;
            this.payBusinessEnum = payBusinessEnum;
        }


        public Builder platform(Integer val) {
            platform = val;
            return this;
        }


        public Builder aliPayParameterMap(Map<String, String> val) {
            aliPayParameterMap = val;
            return this;
        }

        public Builder wechatXmlResult(String val) {
            wechatXmlResult = val;
            return this;
        }

        public PayCallbackModel build() {
            return new PayCallbackModel(this);
        }
    }
}
