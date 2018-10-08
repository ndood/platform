package com.fulu.game.play.service.impl;

import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.common.enums.PlatFormMoneyTypeEnum;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.OrderShareProfitServiceImpl;
import com.fulu.game.play.service.impl.H5FenqilePayServiceImpl;
import com.fulu.game.thirdparty.fenqile.service.FenqileSdkOrderService;
import com.github.binarywang.wxpay.exception.WxPayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
public class H5OrderShareProfitServiceImpl extends OrderShareProfitServiceImpl {


    @Autowired
    private H5FenqilePayServiceImpl h5FenqilePayService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private PilotOrderService pilotOrderService;
    @Autowired
    private MoneyDetailsService moneyDetailsService;
    @Autowired
    private PlatformMoneyDetailsService platformMoneyDetailsService;
    @Autowired
    private UserAutoReceiveOrderService userAutoReceiveOrderService;
    @Autowired
    private FenqileOrderService fenqileOrderService;
    @Autowired
    private FenqileSdkOrderService fenqileSdkOrderService;

    @Override
    public Boolean refund(Order order, BigDecimal actualMoney, BigDecimal refundUserMoney) throws WxPayException {
        return h5FenqilePayService.refund(order.getOrderNo(), actualMoney, refundUserMoney);
    }

    @Override
    public void shareProfit(Order order) {
        BigDecimal totalMoney = order.getTotalMoney();
        BigDecimal charges = order.getCharges();
        if (charges == null) {
            Category category = categoryService.findById(order.getCategoryId());
            charges = category.getCharges();
        }
        BigDecimal commissionMoney = totalMoney.multiply(charges);
        BigDecimal serverMoney = order.getTotalMoney().subtract(commissionMoney);
        //TODO 领航订单做兼容
        //如果是领航订单则用原始的订单金额给打手分润
        PilotOrder pilotOrder = pilotOrderService.findByOrderNo(order.getOrderNo());
        if (pilotOrder != null) {
            serverMoney = pilotOrder.getTotalMoney().subtract(commissionMoney);
            pilotOrder.setIsComplete(true);
            pilotOrderService.update(pilotOrder);
        }
        //记录分润表
        OrderShareProfit orderShareProfit = new OrderShareProfit();
        orderShareProfit.setCommissionMoney(commissionMoney);
        orderShareProfit.setServerMoney(serverMoney);
        orderShareProfit.setUserMoney(new BigDecimal(0));
        orderShareProfit.setOrderNo(order.getOrderNo());
        orderShareProfit.setCreateTime(new Date());
        orderShareProfit.setUpdateTime(new Date());
        create(orderShareProfit);
        //记录用户加零钱
        moneyDetailsService.orderSave(serverMoney, order.getServiceUserId(), order.getOrderNo());
        //平台记录支付打手流水
        platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.ORDER_SHARE_PROFIT, order.getOrderNo(), serverMoney.negate());
        if (OrderTypeEnum.POINT.getType().equals(order.getType())) {
            userAutoReceiveOrderService.addOrderCompleteNum(order.getServiceUserId(), order.getCategoryId());
        }

        try {
            //订单完成通知分期乐完成订单
            FenqileOrder fenqileOrder = fenqileOrderService.findByOrderNo(order.getOrderNo());
            if(fenqileOrder==null){
                log.info("没有对应的分期乐订单:order:{}",order);
            }
            fenqileSdkOrderService.completeFenqileOrder(order.getOrderNo(),fenqileOrder.getFenqileNo());
        }catch (Exception e){
            log.error("订单分润异常",e);
        }
    }
}
