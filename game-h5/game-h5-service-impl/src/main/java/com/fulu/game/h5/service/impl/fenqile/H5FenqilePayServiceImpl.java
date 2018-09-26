package com.fulu.game.h5.service.impl.fenqile;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.ThirdpartyUser;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.ThirdpartyUserService;
import com.fulu.game.core.service.impl.pay.PayServiceImpl;
import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderNotice;
import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderRequest;
import com.fulu.game.thirdparty.fenqile.service.FenqileOrderService;
import com.github.binarywang.wxpay.exception.WxPayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class H5FenqilePayServiceImpl extends PayServiceImpl<FenqileOrderNotice> {

    @Autowired
    private FenqileOrderService fenqileOrderService;
    @Autowired
    private ThirdpartyUserService thirdpartyUserService;
    @Autowired
    private H5OrderServiceImpl h5OrderService;


    @Override
    protected void payOrder(String orderNo, BigDecimal actualMoney) {
        h5OrderService.payOrder(orderNo,actualMoney);
    }

    @Override
    protected Object pay(Order order, User user, String ip) {
        ThirdpartyUser thirdpartyUser = thirdpartyUserService.findByUserId(user.getId());
        FenqileOrderRequest fenqileOrderRequest = new FenqileOrderRequest();
        fenqileOrderRequest.setSubject(order.getName());
        fenqileOrderRequest.setThirdOrderId(order.getOrderNo());
        fenqileOrderRequest.setSkuId("MES201809252323331");
        fenqileOrderRequest.setThirdUid(thirdpartyUser.getFqlOpenid());
        fenqileOrderRequest.setAmount(order.getActualMoney());
        fenqileOrderRequest.setCreateTime(DateUtil.now());
        String result = fenqileOrderService.createOrder(fenqileOrderRequest);
        return result;
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
