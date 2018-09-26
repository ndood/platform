package com.fulu.game.h5.service.impl.fenqile;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.ThirdpartyUser;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.ThirdpartyUserService;
import com.fulu.game.core.service.impl.pay.PayServiceImpl;
import com.fulu.game.thirdparty.fenqile.entity.CodeSessionResult;
import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderNotice;
import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderRequest;
import com.fulu.game.thirdparty.fenqile.service.FenqileOrderService;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.exception.WxPayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class H5FenqilePayServiceImpl extends PayServiceImpl<FenqileOrderNotice> {

    @Autowired
    private FenqileOrderService fenqileOrderService;
    @Autowired
    private ThirdpartyUserService thirdpartyUserService;
    @Autowired
    private H5OrderServiceImpl h5OrderService;


    @Override
    protected void payOrder(String orderNo, BigDecimal actualMoney) {
        h5OrderService.payOrder(orderNo,actualMoney);
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
        String result = fenqileOrderService.createOrder(fenqileOrderRequest);
        return result;
    }



    @Override
    public String payResult(String xmlResult) {
        try {
            FenqileOrderNotice result = parseResult(xmlResult);
            // 结果正确
            String orderNo = getOrderNo(result);
            String totalYuan = getTotal(result);
            if("12".equals(result.getMerchSaleState())){
                payOrder(orderNo, new BigDecimal(totalYuan));
            }else if("15".equals(result.getMerchSaleState())){
                //todo 取消订单
            }
            return "success";
        } catch (Exception e) {
            log.error("回调报文:{}", xmlResult);
            log.error("回调结果异常,异常原因:", e);
            return "error";
        }
    }


    @Override
    protected FenqileOrderNotice parseResult(String result) throws WxPayException {
        //{"sign":"45f6ba64d0d2247f468d85242f1cb9f2","amount":0.01,"subject":"绝地求生：刺激战场 1*局","third_order_id":"TEST180926997162","merch_sale_state":10,"order_id":"O20180926620566103844"}
        JSONObject jso = JSONObject.parseObject(result);
        FenqileOrderNotice fenqileOrderNotice = BeanUtil.fillBeanWithMap(jso.getInnerMap(),new FenqileOrderNotice(), true,true);
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
    protected boolean thirdRefund(String orderNo, Integer totalMoney, Integer refundMoney) throws WxPayException {
        return false;
    }
}
