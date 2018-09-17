package com.fulu.game.common.enums;

import com.fulu.game.common.exception.PayException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 虚拟币和余额充值订单表--支付方式枚举类
 *
 * @author Gong ZeChun
 * @date 2018/9/10 14:52
 */
@Getter
@AllArgsConstructor
public enum PaymentEnum implements TypeEnum<Integer> {
    WECHAT_PAY(1, "微信支付"),
    BALANCE_PAY(2, "账户余额支付"),
    ALIPAY_PAY(3, "支付宝支付"),
    APPLE_STORE_PAY(4, "苹果内购支付");

    private Integer type;
    private String msg;



    public static PaymentEnum getEnumByType(Integer type){
        for(PaymentEnum paymentEnum : PaymentEnum.values()){
            if(paymentEnum.getType().equals(type)){
                return paymentEnum;
            }
        }
        throw new PayException(PayException.ExceptionCode.PAYMENT_UN_MATCH);
    }
}
