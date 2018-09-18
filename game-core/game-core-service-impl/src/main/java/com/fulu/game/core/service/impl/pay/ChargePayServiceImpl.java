package com.fulu.game.core.service.impl.pay;


import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.fulu.game.common.enums.PayBusinessEnum;
import com.fulu.game.common.enums.PaymentEnum;
import com.fulu.game.common.exception.PayException;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.entity.vo.PayRequestVO;
import com.fulu.game.core.service.impl.payment.AlipayPaymentComponent;
import com.fulu.game.core.service.impl.payment.BalancePaymentComponent;
import com.fulu.game.core.service.impl.payment.WeChatPayPaymentComponent;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChargePayServiceImpl {

    @Autowired
    private BalancePaymentComponent balancePayment;
    @Autowired
    private AlipayPaymentComponent alipayPayment;
    @Autowired
    private WeChatPayPaymentComponent weChatPayPayment;










}
