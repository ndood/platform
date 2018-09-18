package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.PaymentEnum;
import com.fulu.game.common.enums.VirtualPayOrderPayPathEnum;
import com.fulu.game.common.enums.VirtualPayOrderTypeEnum;
import com.fulu.game.common.exception.PayException;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualPayOrderDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.entity.vo.VirtualPayOrderVO;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.VirtualPayOrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
@Slf4j
public class VirtualPayOrderServiceImpl extends AbsCommonService<VirtualPayOrder, Integer> implements VirtualPayOrderService {

    @Autowired
    private VirtualPayOrderDao virtualPayOrderDao;
    @Autowired
    private UserService userService;

    @Override
    public ICommonDao<VirtualPayOrder, Integer> getDao() {
        return virtualPayOrderDao;
    }




    public VirtualPayOrder createOrder(String sessionkey,
                                       Integer virtualMoney,
                                       String ip,
                                       Integer payment){
        User user = userService.getCurrentUser();
        BigDecimal actualMoney = new BigDecimal(virtualMoney).divide(new BigDecimal(10)).setScale(2, BigDecimal.ROUND_HALF_UP);
        VirtualPayOrder order = new VirtualPayOrder();
        order.setName("虚拟币充值订单：付款金额：¥" + actualMoney + "，虚拟币数量：" + virtualMoney + "");
        order.setOrderNo(generateVirtualPayOrderNo());
        order.setUserId(user.getId());
        order.setType(VirtualPayOrderTypeEnum.VIRTUAL_ORDER.getType());
        order.setActualMoney(actualMoney);
        order.setVirtualMoney(virtualMoney);
        order.setPayment(payment);
        order.setOrderIp(ip);
        order.setIsPayCallback(false);
        order.setUpdateTime(DateUtil.date());
        order.setCreateTime(DateUtil.date());
        order.setPayPath(VirtualPayOrderPayPathEnum.MP.getType());
        //创建订单
        create(order);
        return order;
    }



    public VirtualPayOrder createBalanceCharge(String sessionkey, BigDecimal money, String ip) {
        User user = userService.getCurrentUser();

        if (money.doubleValue() <= 0) {
            log.error("充值金额money:{}", money);
            throw new PayException(PayException.ExceptionCode.CHARGE_VALUE_ERROR);
        }

        VirtualPayOrder order = new VirtualPayOrder();
        order.setOrderNo(generateVirtualPayOrderNo());
        order.setName("余额充值订单：付款金额：¥" + money + "，充值金额：¥" + money + "");
        order.setUserId(user.getId());
        order.setType(VirtualPayOrderTypeEnum.BALANCE_ORDER.getType());
        order.setPayment(PaymentEnum.WECHAT_PAY.getType());
        order.setActualMoney(money);
        order.setMoney(money);
        order.setOrderIp(ip);
        order.setIsPayCallback(false);
        order.setUpdateTime(DateUtil.date());
        order.setCreateTime(DateUtil.date());
        order.setPayPath(VirtualPayOrderPayPathEnum.MP.getType());

        //创建订单
        create(order);

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
                vo.setPayPath(vo.getPayPath() == null ? VirtualPayOrderPayPathEnum.MP.getType() : vo.getPayPath());
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
