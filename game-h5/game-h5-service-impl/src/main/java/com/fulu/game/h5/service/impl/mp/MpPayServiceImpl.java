package com.fulu.game.h5.service.impl.mp;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.config.WxMpServiceSupply;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.DataException;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.exception.VirtualProductException;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualDetails;
import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.core.service.impl.VirtualPayOrderServiceImpl;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 公众号支付服务类
 *
 * @author Gong ZeChun
 * @date 2018/9/5 14:14
 */
@Service
@Slf4j
public class MpPayServiceImpl extends VirtualPayOrderServiceImpl {
    @Autowired
    private PlatformMoneyDetailsService platformMoneyDetailsService;
    @Autowired
    private OrderMoneyDetailsService orderMoneyDetailsService;
    @Qualifier("virtualPayOrderServiceImpl")
    @Autowired
    private VirtualPayOrderService virtualPayOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private WxMpServiceSupply wxMpServiceSupply;
    @Autowired
    private VirtualDetailsService virtualDetailsService;

    /**
     * 提交充值订单
     *
     * @param sessionkey   订单校验令牌
     * @param actualMoney  实付金额（人民币）
     * @param virtualMoney 充值虚拟币金额（钻石）
     * @param ip           ip
     * @return 订单号
     */
    public Map<String, Object> submit(String sessionkey, BigDecimal actualMoney, Integer virtualMoney, String ip, Integer payType) {
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
        virtualPayOrderService.create(order);
        redisOpenService.delete(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey));

        Map<String, Object> resultMap = new HashMap<>(4);
        if (payType.equals(VirtualPayOrderTypeEnum.WECHAT_PAY.getType())) {
            order.setPayType(VirtualPayOrderTypeEnum.WECHAT_PAY.getType());
            resultMap.put("orderNo", order.getOrderNo());
            resultMap.put("paySuccess", 0);
        } else {
            order.setPayType(VirtualPayOrderTypeEnum.BALANCE_PAY.getType());
            balancePay(order.getOrderNo());
            resultMap.put("orderNo", order.getOrderNo());
            resultMap.put("paySuccess", 1);
        }
        return resultMap;
    }

    public Object payOrder(VirtualPayOrder order, User user, String requestIp) {
        if (order.getIsPayCallback()) {
            throw new OrderException(order.getOrderNo(), "已支付的订单不能支付!");
        }

        Integer totalFee = (order.getActualMoney().multiply(new BigDecimal(100))).intValue();
        //如果订单金额为0,则直接调用支付成功接口
        if (totalFee.equals(0)) {
            successPayOrder(order.getOrderNo(), order.getActualMoney());
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

    private WxPayMpOrderResult pay(VirtualPayOrder order, User user, String ip) {
        WxPayUnifiedOrderRequest orderRequest = buildWxPayRequest(order, ip);
        try {
            orderRequest.setOpenid(user.getOpenId());
            return wxMpServiceSupply.wxMpPayService().createOrder(orderRequest);
        } catch (Exception e) {
            log.error("虚拟币充值订单支付错误", e);
            throw new OrderException(orderRequest.getOutTradeNo(), "虚拟币充值订单无法支付!");
        }
    }

    /**
     * 支付成功
     *
     * @param orderNo     订单号
     * @param actualMoney 实付金额
     * @return 订单Bean
     */
    private VirtualPayOrder successPayOrder(String orderNo, BigDecimal actualMoney) {
        log.info("用户支付订单orderNo:{},actualMoney:{}", orderNo, actualMoney);
        VirtualPayOrder order = virtualPayOrderService.findByOrderNo(orderNo);
        if (order.getIsPayCallback()) {
            throw new OrderException(orderNo, "重复支付订单![" + order.toString() + "]");
        }
        order.setIsPayCallback(true);
        order.setPayTime(DateUtil.date());
        order.setUpdateTime(DateUtil.date());
        order.setCreateTime(DateUtil.date());
        virtualPayOrderService.update(order);
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

    private String generateVirtualPayOrderNo() {
        String orderNo = "C_" + GenIdUtil.GetOrderNo();
        if (virtualPayOrderService.findByOrderNo(orderNo) == null) {
            return orderNo;
        } else {
            return generateVirtualPayOrderNo();
        }
    }

    private VirtualPayOrder balancePay(String orderNo) {
        Integer userId = userService.getCurrentUser().getId();
        User user = userService.findById(userId);
        if (user == null) {
            log.info("当前用户id：{}查询数据库不存在", userId);
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }

        BigDecimal balance = user.getBalance();
        VirtualPayOrder order = virtualPayOrderService.findByOrderNo(orderNo);
        BigDecimal actualMoney = order.getActualMoney();
        if (balance.compareTo(actualMoney) <= 0) {
            log.error("用户userId：{}的账户余额不够充钻石，余额：{}，应付金额：{}", userId, balance, actualMoney);
            throw new VirtualProductException(VirtualProductException.ExceptionCode.BALANCE_NOT_ENOUGH_EXCEPTION);
        }

        order.setIsPayCallback(true);
        order.setPayTime(DateUtil.date());
        order.setUpdateTime(DateUtil.date());
        virtualPayOrderService.update(order);

        user.setBalance(balance.subtract(actualMoney));
        user.setVirtualBalance((user.getVirtualBalance() == null ? 0 : user.getVirtualBalance())
                + order.getVirtualMoney());
        user.setUpdateTime(DateUtil.date());
        userService.update(user);

        //记录虚拟币流水
        VirtualDetails details = new VirtualDetails();
        details.setUserId(userId);
        details.setRelevantNo(order.getOrderNo());
        details.setSum(user.getVirtualBalance());
        details.setMoney(order.getVirtualMoney());
        details.setType(VirtualDetailsTypeEnum.VIRTUAL_MONEY.getType());
        details.setRemark(VirtualDetailsRemarkEnum.CHARGE.getMsg());
        details.setCreateTime(DateUtil.date());
        virtualDetailsService.create(details);

        //记录平台流水
        platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.VIRTUAL_ORDER_PAY,
                order.getOrderNo(),
                order.getActualMoney());

        //记录订单流水
        orderMoneyDetailsService.create(order.getOrderNo(),
                order.getUserId(),
                DetailsEnum.VIRTUAL_ORDER_PAY,
                actualMoney);

        return order;
    }
}
