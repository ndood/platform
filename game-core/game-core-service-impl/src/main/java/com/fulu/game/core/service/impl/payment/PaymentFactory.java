package com.fulu.game.core.service.impl.payment;

import com.fulu.game.common.enums.PaymentEnum;
import com.fulu.game.common.exception.PayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by burgl on 2018/9/18.
 */

@Service
public class PaymentFactory {

    @Autowired
    private AlipayPaymentComponent alipayPaymentComponent;
    @Autowired
    private BalancePaymentComponent balancePaymentComponent;
    @Autowired
    private WeChatPayPaymentComponent weChatPayPaymentComponent;
    @Autowired
    private FenqilePaymentComponent fenqilePaymentComponent;

    public PaymentComponent build(Integer payment) {
        PaymentEnum paymentEnum = PaymentEnum.getEnumByType(payment);
        switch (paymentEnum) {
            case ALIPAY_PAY:
                return alipayPaymentComponent;
            case WECHAT_PAY:
                return weChatPayPaymentComponent;
            case BALANCE_PAY:
                return balancePaymentComponent;
            case FENQILE_PAY:
                return fenqilePaymentComponent;
            default:
                throw new PayException(PayException.ExceptionCode.PAYMENT_UN_MATCH);
        }
    }


}
