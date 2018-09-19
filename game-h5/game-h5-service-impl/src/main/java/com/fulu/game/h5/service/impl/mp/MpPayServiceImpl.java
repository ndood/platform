package com.fulu.game.h5.service.impl.mp;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.config.WxMpServiceSupply;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.*;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.entity.MoneyDetails;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualDetails;
import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.core.service.impl.VirtualPayOrderServiceImpl;
import com.fulu.game.core.service.impl.payment.BalancePaymentComponent;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.exception.WxPayException;
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

    @Autowired
    private BalancePaymentComponent balancePayment;




    /**
     * 提交充值订单
     *
     * @param sessionkey   订单校验令牌
     * @param virtualMoney 充值虚拟币金额（钻石）
     * @param ip           ip
     * @return 订单号
     */
    public Map<String, Object> submit(String sessionkey,
                                      Integer virtualMoney,
                                      String ip,
                                      Integer payment) {
        User user = userService.getCurrentUser();
        if (!redisOpenService.hasKey(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey))) {
            log.error("验证sessionkey错误:sessionkey:{};virtualMoney:{};ip:{};userId:{}",
                    sessionkey, virtualMoney, ip, user.getId());
            throw new DataException(DataException.ExceptionCode.NO_FORM_TOKEN_ERROR);
        }

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
        virtualPayOrderService.create(order);
        redisOpenService.delete(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey));

        Map<String, Object> resultMap = new HashMap<>(4);
        if (payment.equals(PaymentEnum.WECHAT_PAY.getType())) {
            resultMap.put("orderNo", order.getOrderNo());
            resultMap.put("paySuccess", 0);
        } else {
            balancePay(order.getOrderNo());
            resultMap.put("orderNo", order.getOrderNo());
            resultMap.put("paySuccess", 1);
        }
        return resultMap;
    }

    public WxPayMpOrderResult payOrder(VirtualPayOrder order, User user, String requestIp) {
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
            e.printStackTrace();
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
            orderRequest.setOpenid(user.getPublicOpenId());
            return wxMpServiceSupply.wxMpPayService().createOrder(orderRequest);
        } catch (Exception e) {
            log.error("虚拟币充值订单支付错误", e);
            throw new OrderException(orderRequest.getOutTradeNo(), "虚拟币充值订单无法支付!");
        }
    }



    private String generateVirtualPayOrderNo() {
        String orderNo = "C_" + GenIdUtil.GetOrderNo();
        if (virtualPayOrderService.findByOrderNo(orderNo) == null) {
            return orderNo;
        } else {
            return generateVirtualPayOrderNo();
        }
    }

    /**
     * 余额支付虚拟币的充值订单
     *
     * @param orderNo 订单号
     * @return 虚拟币充值订单Bean
     */
    private VirtualPayOrder balancePay(String orderNo) {
        Integer userId = userService.getCurrentUser().getId();
        User user = userService.findById(userId);
        if (user == null) {
            log.info("当前用户id：{}查询数据库不存在", userId);
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }

        VirtualPayOrder order = virtualPayOrderService.findByOrderNo(orderNo);
        BigDecimal actualMoney = order.getActualMoney();

        boolean payResult = balancePayment.balancePayVirtualMoney(userId, actualMoney, orderNo);
        if (!payResult) {
            log.error("余额支付虚拟币，支付失败，用户id：{}，订单号：{}", userId, orderNo);
            throw new PayException(PayException.ExceptionCode.PAY_FAIL);
        }

        BigDecimal chargeBalance = user.getChargeBalance() == null ? BigDecimal.ZERO : user.getChargeBalance();

        order.setIsPayCallback(true);
        order.setPayTime(DateUtil.date());
        order.setUpdateTime(DateUtil.date());
        virtualPayOrderService.update(order);

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

    private WxPayOrderNotifyResult parseResult(String xmlResult) throws WxPayException {
        return wxMpServiceSupply.wxMpPayService().parseOrderNotifyResult(xmlResult);
    }

    private String getOrderNo(WxPayOrderNotifyResult result) {
        return result.getOutTradeNo();
    }

    private String getTotal(WxPayOrderNotifyResult result) {
        return BaseWxPayResult.feeToYuan(result.getTotalFee());
    }

    public String payResult(String xmlResult) {
        try {
            WxPayOrderNotifyResult result = parseResult(xmlResult);
            // 结果正确
            String orderNo = getOrderNo(result);
            String totalYuan = getTotal(result);
            successPayOrder(orderNo, new BigDecimal(totalYuan));
            return WxPayNotifyResponse.success("处理成功!");
        } catch (Exception e) {
            log.error("回调报文:{}", xmlResult);
            log.error("回调结果异常,异常原因:", e);
            return WxPayNotifyResponse.fail(e.getMessage());
        }
    }

    /**
     * @param sessionkey 订单校验令牌
     * @param money      充值到平台的金额
     * @param ip         ip地址字符串
     * @return 结果集Map
     */
    public Map<String, Object> balanceCharge(String sessionkey, BigDecimal money, String ip) {
        User user = userService.getCurrentUser();
        if (!redisOpenService.hasKey(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey))) {
            log.error("验证sessionkey错误:sessionkey:{};money:{};ip:{};userId:{}",
                    sessionkey, money, ip, user.getId());
            throw new DataException(DataException.ExceptionCode.NO_FORM_TOKEN_ERROR);
        }

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
        virtualPayOrderService.create(order);
        redisOpenService.delete(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey));

        Map<String, Object> resultMap = new HashMap<>(4);
        resultMap.put("orderNo", order.getOrderNo());
        return resultMap;
    }
}
