package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.config.WxMpServiceSupply;
import com.fulu.game.common.enums.DetailsEnum;
import com.fulu.game.common.enums.PlatFormMoneyTypeEnum;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.DataException;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualPayOrderDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.service.OrderMoneyDetailsService;
import com.fulu.game.core.service.PlatformMoneyDetailsService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.VirtualPayOrderService;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;


@Service
@Slf4j
public class VirtualPayOrderServiceImpl extends AbsCommonService<VirtualPayOrder, Integer> implements VirtualPayOrderService {

    @Autowired
    private VirtualPayOrderDao virtualPayOrderDao;
    @Autowired
    private VirtualPayOrderService virtualPayOrderService;

    @Autowired
    private WxMpServiceSupply wxMpServiceSupply;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private PlatformMoneyDetailsService platformMoneyDetailsService;
    @Autowired
    private OrderMoneyDetailsService orderMoneyDetailsService;

    @Override
    public ICommonDao<VirtualPayOrder, Integer> getDao() {
        return virtualPayOrderDao;
    }

    public String submit(String sessionkey, BigDecimal actualMoney, Integer virtualMoney, String ip) {
        User user = userService.getCurrentUser();
        if (!redisOpenService.hasKey(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey))) {
            log.error("验证sessionkey错误:sessionkey:{};actualMoney:{};virtualMoney:{};ip:{};userId:{}",
                    sessionkey, actualMoney, virtualMoney, ip, user.getId());
            throw new DataException(DataException.ExceptionCode.NO_FORM_TOKEN_ERROR);
        }

        VirtualPayOrder order = new VirtualPayOrder();
        order.setName("虚拟币充值订单：付款金额：¥" + actualMoney + "，虚拟币数量：" + virtualMoney + "");
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

    public Object pay(String orderNo, String ip) {
        VirtualPayOrder order = findByOrderNo(orderNo);
        User user = userService.findById(order.getUserId());
        return payOrder(order, user, ip);
    }

    public Object payOrder(VirtualPayOrder order, User user, String requestIp) {
        if (order.getIsPayCallback()) {
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

    /**
     * 支付成功
     *
     * @param orderNo     订单号
     * @param actualMoney 实付金额
     * @return 订单Bean
     */
    private VirtualPayOrder payOrder(String orderNo, BigDecimal actualMoney) {
        log.info("用户支付订单orderNo:{},actualMoney:{}", orderNo, actualMoney);
        VirtualPayOrder order = findByOrderNo(orderNo);
        if (order.getIsPayCallback()) {
            throw new OrderException(orderNo, "重复支付订单![" + order.toString() + "]");
        }
        order.setIsPayCallback(true);
        order.setPayTime(DateUtil.date());
        order.setUpdateTime(DateUtil.date());
        order.setCreateTime(DateUtil.date());
        update(order);
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

    private WxPayMpOrderResult pay(VirtualPayOrder order, User user, String ip) {
        WxPayUnifiedOrderRequest orderRequest = buildWxPayRequest(order, ip);
        try {
            orderRequest.setOpenid(user.getOpenId());
            return wxMpServiceSupply.wxMpPayService().createOrder(orderRequest);
        } catch (Exception e) {
            log.error("虚拟订单支付错误", e);
            throw new OrderException(orderRequest.getOutTradeNo(), "虚拟订单无法支付!");
        }
    }

    /**
     * 构造微信支付请求request
     *
     * @param order 订单
     * @param ip    请求ip
     * @return 支付request
     */
    protected WxPayUnifiedOrderRequest buildWxPayRequest(VirtualPayOrder order, String ip) {
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody(order.getName());
        orderRequest.setOutTradeNo(order.getOrderNo());
        orderRequest.setTotalFee((order.getActualMoney().multiply(new BigDecimal(100))).intValue());//元转成分
        orderRequest.setSpbillCreateIp(ip);
        orderRequest.setTimeStart(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        return orderRequest;
    }

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

    private String generateVirtualPayOrderNo() {
        String orderNo = "C_" + GenIdUtil.GetOrderNo();
        if (virtualPayOrderService.findByOrderNo(orderNo) == null) {
            return orderNo;
        } else {
            return generateVirtualPayOrderNo();
        }
    }
}
