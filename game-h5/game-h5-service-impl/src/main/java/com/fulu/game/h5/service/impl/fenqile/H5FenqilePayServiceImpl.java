package com.fulu.game.h5.service.impl.fenqile;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.PayException;
import com.fulu.game.core.entity.FenqileOrder;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.ThirdpartyUser;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.FenqileOrderService;
import com.fulu.game.core.service.ThirdpartyUserService;
import com.fulu.game.core.service.impl.pay.PayServiceImpl;
import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderNotice;
import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderRequest;
import com.fulu.game.thirdparty.fenqile.service.FenqileSdkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
public class H5FenqilePayServiceImpl extends PayServiceImpl<FenqileOrderNotice> {

    @Autowired
    private FenqileSdkOrderService fenqileSdkOrderService;
    @Autowired
    private ThirdpartyUserService thirdpartyUserService;
    @Autowired
    private H5OrderServiceImpl h5OrderService;
    @Autowired
    private FenqileOrderService fenqileOrderService;


    @Override
    protected void payOrder(String orderNo, BigDecimal actualMoney) {
        h5OrderService.payOrder(orderNo, actualMoney);
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
        String result = fenqileSdkOrderService.createOrder(fenqileOrderRequest);
        return result;
    }


    @Override
    public String payResult(String xmlResult) {
        try {
            FenqileOrderNotice result = parseResult(xmlResult);
            // 结果正确
            String orderNo = getOrderNo(result);
            String totalYuan = getTotal(result);
            if (Integer.valueOf(12).equals(result.getMerchSaleState())) {
                payOrder(orderNo, new BigDecimal(totalYuan));
            } else if (Integer.valueOf(15).equals(result.getMerchSaleState())) {
                //todo 取消订单
                h5OrderService.fenqileUserCancelOrder(orderNo);
            } else if (Integer.valueOf(10).equals(result.getMerchSaleState())) {
                FenqileOrder origOrder = fenqileOrderService.findByOrderNo(orderNo);
                if (origOrder == null) {
                    throw new OrderException(OrderException.ExceptionCode.ORDER_NOT_EXIST, origOrder.getOrderNo());
                } else {
                    //更新分期乐订单数据
                    origOrder.setFenqileNo(result.getOrderId());
                    origOrder.setUpdateTime(new Date());
                    fenqileOrderService.update(origOrder);
                }
            }
            return "{\"result\":0}";
        } catch (Exception e) {
            log.error("回调报文:{}", xmlResult);
            log.error("回调结果异常,异常原因:", e);
            return "{\"result\":1}";
        }
    }


    @Override
    protected FenqileOrderNotice parseResult(String result) {
        //{"sign":"45f6ba64d0d2247f468d85242f1cb9f2","amount":0.01,"subject":"绝地求生：刺激战场 1*局","third_order_id":"TEST180926997162","merch_sale_state":10,"order_id":"O20180926620566103844"}
        JSONObject jso = JSONObject.parseObject(result);
        FenqileOrderNotice fenqileOrderNotice = BeanUtil.fillBeanWithMap(jso.getInnerMap(), new FenqileOrderNotice(), true, true);
        return fenqileOrderNotice;
    }

    @Override
    protected String getOrderNo(FenqileOrderNotice result) {
        return result.getThirdOrderId();
    }

    @Override
    protected String getTotal(FenqileOrderNotice result) {
        return result.getAmount().toPlainString();
    }

    @Override
    protected boolean thirdRefund(String orderNo, Integer totalMoney, Integer refundMoney) {
        FenqileOrder fenqileOrder = fenqileOrderService.findByOrderNo(orderNo);
        if (fenqileOrder == null) {
            throw new PayException(PayException.ExceptionCode.THIRD_REFUND_FAIL, orderNo);
        }
        if (totalMoney.equals(refundMoney)) {
            log.info("调用分期乐全部退款:fenqileOrder:{},totalMoney:{},refundMoney:{}", fenqileOrder, totalMoney, refundMoney);
            return fenqileSdkOrderService.cancelFenqileOrder(orderNo, fenqileOrder.getFenqileNo());
        } else {
            log.info("调用分期乐部分退款:fenqileOrder:{},totalMoney:{},refundMoney:{}", fenqileOrder, totalMoney, refundMoney);
            return fenqileSdkOrderService.noticeFenqileRefund(orderNo, fenqileOrder.getFenqileNo(), new BigDecimal(Double.valueOf(refundMoney) / 100).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
    }

}
