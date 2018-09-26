package com.fulu.game.h5.service.impl.fenqile;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.impl.pay.PayServiceImpl;
import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderNotice;
import com.github.binarywang.wxpay.exception.WxPayException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class H5FenqilePayServiceImpl extends PayServiceImpl<FenqileOrderNotice> {


    @Override
    protected void payOrder(String orderNo, BigDecimal actualMoney) {

    }

    @Override
    protected Object pay(Order order, User user, String ip) {



        return null;
    }

    @Override
    protected FenqileOrderNotice parseResult(String xmlResult) throws WxPayException {
        return null;
    }

    @Override
    protected String getOrderNo(FenqileOrderNotice result) {
        return null;
    }

    @Override
    protected String getTotal(FenqileOrderNotice result) {
        return null;
    }

    @Override
    protected boolean thirdRefund(String orderNo, Integer totalMoney, Integer refundMoney) throws WxPayException {
        return false;
    }
}
