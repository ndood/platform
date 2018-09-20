package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.PayException;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualPayOrderDao;
import com.fulu.game.core.entity.MoneyDetails;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualDetails;
import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.entity.vo.VirtualPayOrderVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class VirtualPayOrderServiceImpl extends AbsCommonService<VirtualPayOrder, Integer> implements VirtualPayOrderService {

    @Autowired
    private VirtualPayOrderDao virtualPayOrderDao;
    @Autowired
    private UserService userService;
    @Autowired
    private VirtualDetailsService virtualDetailsService;
    @Autowired
    private MoneyDetailsService moneyDetailsService;
    @Autowired
    private PlatformMoneyDetailsService platformMoneyDetailsService;
    @Autowired
    private OrderMoneyDetailsService orderMoneyDetailsService;

    @Override
    public ICommonDao<VirtualPayOrder, Integer> getDao() {
        return virtualPayOrderDao;
    }


    /**
     * 钻石充值
     *
     * @param virtualMoney
     * @param ip
     * @param payment
     * @return
     */
    @Override
    public VirtualPayOrder diamondCharge(Integer userId, Integer virtualMoney, Integer payment, Integer payPath, String ip) {
        BigDecimal actualMoney = new BigDecimal(virtualMoney).divide(new BigDecimal(10)).setScale(2, BigDecimal.ROUND_HALF_UP);
        VirtualPayOrder order = new VirtualPayOrder();
        order.setName("虚拟币充值订单：付款金额：¥" + actualMoney + "，虚拟币数量：" + virtualMoney + "");
        order.setOrderNo(generateVirtualPayOrderNo());
        order.setUserId(userId);
        order.setActualMoney(actualMoney);
        order.setVirtualMoney(virtualMoney);
        order.setPayment(payment);
        order.setOrderIp(ip);
        order.setPayPath(payPath);
        //创建订单
        return create(order, VirtualPayOrderTypeEnum.VIRTUAL_ORDER);
    }


    /**
     * 余额充值
     *
     * @param money
     * @param ip
     * @return
     */
    @Override
    public VirtualPayOrder balanceCharge(Integer userId,
                                         BigDecimal money,
                                         Integer payment,
                                         Integer payPath,
                                         String ip) {
        if (money.doubleValue() <= 0) {
            log.error("充值金额money:{}", money);
            throw new PayException(PayException.ExceptionCode.CHARGE_VALUE_ERROR);
        }
        VirtualPayOrder order = new VirtualPayOrder();
        order.setOrderNo(generateVirtualPayOrderNo());
        order.setName("余额充值订单：付款金额：¥" + money + "，充值金额：¥" + money + "");
        order.setUserId(userId);
        order.setPayment(payment);
        order.setActualMoney(money);
        order.setMoney(money);
        order.setOrderIp(ip);
        order.setPayPath(payPath);
        //创建订单
        return create(order, VirtualPayOrderTypeEnum.BALANCE_ORDER);
    }


    /**
     * 创建充值订单统一接口
     *
     * @param order
     * @param virtualPayOrderTypeEnum
     * @return
     */
    private VirtualPayOrder create(VirtualPayOrder order, VirtualPayOrderTypeEnum virtualPayOrderTypeEnum) {
        order.setType(virtualPayOrderTypeEnum.getType());
        order.setIsPayCallback(false);
        order.setUpdateTime(new Date());
        order.setCreateTime(new Date());
        create(order);
        return order;
    }


    /**
     * 支付成功
     *
     * @param orderNo     订单号
     * @param actualMoney 实付金额
     * @return 订单Bean
     */
    @Override
    public VirtualPayOrder successPayOrder(String orderNo, BigDecimal actualMoney) {
        log.info("用户支付订单orderNo:{},actualMoney:{}", orderNo, actualMoney);
        VirtualPayOrder order = findByOrderNo(orderNo);

        log.info("订单详情：" + order.toString());

        if (order.getIsPayCallback()) {
            throw new OrderException(orderNo, "重复支付订单![" + order.toString() + "]");
        }
        order.setIsPayCallback(true);
        order.setPayTime(DateUtil.date());
        order.setUpdateTime(DateUtil.date());
        update(order);
        Integer type = order.getType();
        User user = userService.findById(order.getUserId());
        if (type.equals(VirtualPayOrderTypeEnum.VIRTUAL_ORDER.getType())) {
            user.setVirtualBalance((user.getVirtualBalance() == null ? 0 : user.getVirtualBalance()) + order.getVirtualMoney());
            user.setUpdateTime(DateUtil.date());
            //记录虚拟币流水
            VirtualDetails details = new VirtualDetails();
            details.setUserId(user.getId());
            details.setRelevantNo(order.getOrderNo());
            details.setSum(user.getVirtualBalance());
            details.setMoney(order.getVirtualMoney());
            details.setType(VirtualDetailsTypeEnum.VIRTUAL_MONEY.getType());
            details.setRemark(VirtualDetailsRemarkEnum.CHARGE.getMsg());
            details.setCreateTime(DateUtil.date());
            virtualDetailsService.create(details);
        } else {
            //余额充值订单，需要记录零钱流水
            MoneyDetails mDetails = new MoneyDetails();
            mDetails.setOperatorId(order.getUserId());
            mDetails.setTargetId(order.getUserId());
            mDetails.setMoney(order.getMoney());
            mDetails.setAction(MoneyOperateTypeEnum.WITHDRAW_BALANCE.getType());
            BigDecimal chargeBalance = user.getChargeBalance() == null ? BigDecimal.ZERO : user.getChargeBalance();
            mDetails.setSum(user.getBalance().add(chargeBalance));
            mDetails.setRemark(MoneyOperateTypeEnum.WITHDRAW_BALANCE.getMsg() + "订单号: " + orderNo);
            mDetails.setCreateTime(DateUtil.date());
            moneyDetailsService.drawSave(mDetails);

            user.setChargeBalance(chargeBalance.add(order.getMoney()));
            user.setUpdateTime(DateUtil.date());
        }
        userService.update(user);
        userService.updateRedisUser(user);

        //记录平台流水
        platformMoneyDetailsService.createOrderDetails(
                PlatFormMoneyTypeEnum.VIRTUAL_ORDER_PAY,
                order.getOrderNo(),
                order.getActualMoney());

        //记录订单流水
        orderMoneyDetailsService.create(order.getOrderNo(), order.getUserId(), DetailsEnum.VIRTUAL_ORDER_PAY, actualMoney);
        //fixme 留言通知推送
        return order;
    }


    @Override
    public VirtualPayOrder findByOrderNo(String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            return null;
        }
        return virtualPayOrderDao.findByOrderNo(orderNo);
    }

    @Override
    public PageInfo<VirtualPayOrderVO> chargeList(VirtualPayOrderVO payOrderVO, Integer pageNum, Integer pageSize, String orderBy) {
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "vpo.pay_time DESC";
        }

        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<VirtualPayOrderVO> voList = virtualPayOrderDao.chargeList(payOrderVO);
        if (CollectionUtils.isNotEmpty(voList)) {
            for (VirtualPayOrderVO vo : voList) {
                vo.setPayPath(vo.getPayPath() == null ? PlatformEcoEnum.MP.getType() : vo.getPayPath());
            }
        }
        return new PageInfo<>(voList);
    }

    @Override
    public PageInfo<VirtualPayOrderVO> chargeList(VirtualPayOrderVO payOrderVO) {
        PageHelper.startPage("vpo.pay_time DESC");
        List<VirtualPayOrderVO> voList = virtualPayOrderDao.chargeList(payOrderVO);
        return new PageInfo<>(voList);
    }


    private String generateVirtualPayOrderNo() {
        String orderNo = "C_" + GenIdUtil.GetOrderNo();
        if (findByOrderNo(orderNo) == null) {
            return orderNo;
        } else {
            return generateVirtualPayOrderNo();
        }
    }
}
