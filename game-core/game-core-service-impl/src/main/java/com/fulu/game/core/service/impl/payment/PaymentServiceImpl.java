package com.fulu.game.core.service.impl.payment;

import com.fulu.game.core.entity.vo.PaymentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private BalancePayServiceImpl balancePayService;

    @Override
    public Boolean paySuccess(PaymentVO paymentVO) {
        boolean flag = false;
        switch (paymentVO.getPaymentEnum()) {
            case BALANCE_PAY:
                flag = balancePayService.balancePay(paymentVO.getPayBusinessEnum(), paymentVO.getUser().getId(), paymentVO.getOrder().getActualMoney(), paymentVO.getOrder().getOrderNo());
                break;
        }
        return flag;
    }


    @Override
    public Object createPayRequest(PaymentVO paymentVO) {

        switch (paymentVO.getPaymentEnum()){

        }


        return null;
    }







}
