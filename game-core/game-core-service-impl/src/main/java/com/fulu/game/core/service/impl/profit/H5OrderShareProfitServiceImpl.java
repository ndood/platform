package com.fulu.game.core.service.impl.profit;

import com.fulu.game.common.enums.PlatFormMoneyTypeEnum;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderShareProfit;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.MoneyDetailsService;
import com.fulu.game.core.service.PlatformMoneyDetailsService;
import com.fulu.game.core.service.impl.OrderShareProfitServiceImpl;
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
    private CategoryService categoryService;
    @Autowired
    private MoneyDetailsService moneyDetailsService;
    @Autowired
    private PlatformMoneyDetailsService platformMoneyDetailsService;


    /**
     * 订单正常完成状态分润
     * @param order
     */
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

    }


    @Override
    protected <T> T refund(Order order, BigDecimal actualMoney, BigDecimal refundUserMoney) throws WxPayException {
        return null;
    }
}
