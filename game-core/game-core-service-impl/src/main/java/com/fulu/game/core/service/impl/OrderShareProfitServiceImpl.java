package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.DetailsEnum;
import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.common.enums.PlatFormMoneyTypeEnum;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.core.dao.ArbitrationDetailsDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.OrderShareProfitDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;


@Service
@Slf4j
public abstract class OrderShareProfitServiceImpl extends AbsCommonService<OrderShareProfit, Integer> implements OrderShareProfitService {

    @Autowired
    private OrderShareProfitDao orderShareProfitDao;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private PilotOrderService pilotOrderService;
    @Autowired
    private MoneyDetailsService moneyDetailsService;
    @Autowired
    private PlatformMoneyDetailsService platformMoneyDetailsService;
    @Autowired
    private OrderMoneyDetailsService orderMoneyDetailsService;
    @Autowired
    private ArbitrationDetailsDao arbitrationDetailsDao;
    @Autowired
    private UserAutoReceiveOrderService userAutoReceiveOrderService;


    @Override
    public ICommonDao<OrderShareProfit, Integer> getDao() {
        return orderShareProfitDao;
    }

    /**
     * 订单正常完成状态分润
     *
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
    }


    /**
     * 订单发生退款
     *
     * @param order
     */
    @Override
    public void orderRefund(Order order, BigDecimal refundMoney) {
        if (!order.getIsPay()) {
            throw new OrderException(order.getOrderNo(), "未支付订单不允许退款!");
        }
        if (order.getActualMoney().compareTo(refundMoney) < 0) {
            throw new OrderException(order.getOrderNo(), "退款金额不能大于用户实付金额!");
        }
        BigDecimal charges = order.getCharges();
        if (charges == null) {
            Category category = categoryService.findById(order.getCategoryId());
            charges = category.getCharges();
        }
        BigDecimal surplusMoney = order.getActualMoney().subtract(refundMoney);
        BigDecimal commissionMoney = surplusMoney.multiply(charges);
        BigDecimal serverMoney = surplusMoney.subtract(commissionMoney);
        //记录分润表
        OrderShareProfit orderShareProfit = new OrderShareProfit();
        orderShareProfit.setCommissionMoney(commissionMoney);
        orderShareProfit.setServerMoney(serverMoney);
        orderShareProfit.setUserMoney(refundMoney);
        orderShareProfit.setOrderNo(order.getOrderNo());
        orderShareProfit.setCreateTime(new Date());
        orderShareProfit.setUpdateTime(new Date());
        create(orderShareProfit);
        //记录平台流水
        platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.ORDER_REFUND, order.getOrderNo(), refundMoney.negate());
        platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.ORDER_REFUND, order.getOrderNo(), serverMoney.negate());
        if (order.getServiceUserId() != null) {
            //记录用户（陪玩师）加零钱
            moneyDetailsService.orderSave(serverMoney, order.getServiceUserId(), order.getOrderNo());
        }
        //记录订单流水
        orderMoneyDetailsService.create(order.getOrderNo(), order.getUserId(), DetailsEnum.ORDER_USER_CANCEL, refundMoney.negate());
        if (!refund(order, order.getActualMoney(), refundMoney)) {
            throw new OrderException(order.getOrderNo(), "订单退款失败!");
        }
        if (OrderTypeEnum.POINT.getType().equals(order.getType())) {
            if (OrderStatusEnum.CONSULT_COMPLETE.getStatus().equals(order.getStatus()) || OrderStatusEnum.SYSTEM_CONSULT_COMPLETE.getStatus().equals(order.getStatus())) {
                userAutoReceiveOrderService.addOrderDisputeNum(order.getServiceUserId(), order.getCategoryId());
            } else {
                if (order.getServiceUserId() != null) {
                    userAutoReceiveOrderService.addOrderCancelNum(order.getServiceUserId(), order.getCategoryId());
                }
            }
        }

    }

    /**
     * 订单金额部分退款给用户，部分退款给陪玩师
     *
     * @param order
     * @param details
     */
    @Override
    public void orderRefundToUserAndServiceUser(Order order, ArbitrationDetails details) {
        log.info("订单协商退款:order:{};details:{}", order, details);

        String orderNo = order.getOrderNo();
        BigDecimal refundUserMoney = details.getRefundUserMoney();

        orderRefund(order, refundUserMoney);

        BigDecimal surplusMoney = order.getActualMoney().subtract(refundUserMoney);

        BigDecimal charges = order.getCharges();
        if (charges == null) {
            Category category = categoryService.findById(order.getCategoryId());
            charges = category.getCharges();
        }
        BigDecimal commissionMoney = surplusMoney.multiply(charges);
        BigDecimal serverMoney = surplusMoney.subtract(commissionMoney);

        //记录仲裁结果流水表
        ArbitrationDetails arbitrationDetails = new ArbitrationDetails();
        arbitrationDetails.setOrderNo(orderNo);
        arbitrationDetails.setUserId(details.getUserId());
        arbitrationDetails.setServiceUserId(details.getServiceUserId());
        arbitrationDetails.setRefundUserMoney(refundUserMoney);
        arbitrationDetails.setRefundServiceUserMoney(serverMoney);
        arbitrationDetails.setCommissionMoney(commissionMoney);
        arbitrationDetails.setRemark(details.getRemark());
        arbitrationDetails.setUpdateTime(DateUtil.date());
        arbitrationDetails.setCreateTime(DateUtil.date());
        arbitrationDetailsDao.create(arbitrationDetails);
    }





    protected abstract Boolean refund(Order order, BigDecimal actualMoney, BigDecimal refundUserMoney);


}
