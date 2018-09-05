package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.SystemException;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualPayOrderDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.VirtualPayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@Slf4j
public class VirtualPayOrderServiceImpl extends AbsCommonService<VirtualPayOrder, Integer> implements VirtualPayOrderService {

    @Autowired
    private VirtualPayOrderDao virtualPayOrderDao;
    @Autowired
    private VirtualPayOrderService virtualPayOrderService;

    @Autowired
    private UserService userService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    @Override
    public ICommonDao<VirtualPayOrder, Integer> getDao() {
        return virtualPayOrderDao;
    }


    @Override
    public String submit(String sessionkey, BigDecimal actualMoney, Integer virtualMoney, String ip) {
        User user = userService.getCurrentUser();
        if (!redisOpenService.hasKey(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey))) {
            log.error("验证sessionkey错误:sessionkey:{};actualMoney:{};virtualMoney:{};ip:{};userId:{}",
                    sessionkey, actualMoney, virtualMoney, ip, user.getId());
            throw new SystemException(SystemException.ExceptionCode.NO_FORM_TOKEN_ERROR);
        }

        VirtualPayOrder order = new VirtualPayOrder();
        order.setOrderNo(generateVirtualPayOrderNo());
        order.setUserId(user.getId());
        order.setActualMoney(actualMoney);
        order.setVirtualMoney(virtualMoney);
        order.setOrderIp(ip);
        order.setIsPayCallback(false);
        order.setUpdateTime(DateUtil.date());
        order.setCreateTime(DateUtil.date());

        //创建订单
        virtualPayOrderDao.create(order);
        redisOpenService.delete(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey));
        return order.getOrderNo();
    }

    @Override
    public Object pay(String orderNo, String ip) {
        VirtualPayOrder order = findByOrderNo(orderNo);
        User user = userService.findById(order.getUserId());
        Object result = payOrder(order, user, ip);
        return result;
    }

    @Override
    public Object payOrder(VirtualPayOrder order, User user, String requestIp) {
        if (!order.getIsPayCallback()) {
            throw new OrderException(order.getOrderNo(), "已支付的订单不能支付!");
        }

        Integer totalFee = (order.getActualMoney().multiply(new BigDecimal(100))).intValue();
        //如果订单金额为0,则直接调用支付成功接口
        if (totalFee.equals(0)) {
            payOrder(order.getOrderNo(), order.getActualMoney());
            return null;
        }
        try {
            log.info("订单支付:orderNo:{};order:{};requestIp:{};", order.getOrderNo(), order, requestIp);
            return pay(order, user, requestIp);
        } catch (Exception e) {
            log.error("订单支付错误", e);
            throw new OrderException(order.getOrderNo(), "订单无法支付!");
        }
    }

    private void payOrder(String orderNo, BigDecimal actualMoney) {


    }

    private Object pay(VirtualPayOrder order, User user, String ip) {

        return null;
    }

    @Override
    public VirtualPayOrder charge(String code, BigDecimal actualMoney, Integer virtualMoney, String mobile) {
//        if (StringUtils.isBlank(code)) {
//            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
//        }
//
//        WxMpService wxMpService = wxMpServiceSupply.getWxMpService();
//        WxMpOAuth2AccessToken token;
//        String openId = null;
//        String unionId = null;
//        try {
//            token = wxMpService.oauth2getAccessToken(code);
//            openId = token.getOpenId();
//            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(token, null);
//            unionId = wxMpUser.getUnionId();
//        } catch (WxErrorException e) {
//            e.printStackTrace();
//            log.error("通过公众号获取用户信息出错", e);
//        }

        //fixme 先判断mobile 然后openId 然后unionId（非空判断）

        return null;
    }

    @Override
    public VirtualPayOrder findByOrderNo(String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            return null;
        }

        return virtualPayOrderDao.findByOrderNo(orderNo);
    }

    public String generateVirtualPayOrderNo() {
        String orderNo = "C_" + GenIdUtil.GetOrderNo();
        if (virtualPayOrderService.findByOrderNo(orderNo) == null) {
            return orderNo;
        } else {
            return generateVirtualPayOrderNo();
        }
    }
}
