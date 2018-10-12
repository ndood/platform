package com.fulu.game.h5.controller.fenqile;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.enums.PayBusinessEnum;
import com.fulu.game.common.enums.PaymentEnum;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.core.entity.FenqileOrder;
import com.fulu.game.core.entity.payment.model.PayCallbackModel;
import com.fulu.game.core.service.FenqileOrderService;
import com.fulu.game.h5.service.impl.H5OrderServiceImpl;
import com.fulu.game.h5.service.impl.H5PayServiceImpl;
import com.fulu.game.thirdparty.fenqile.entity.FenqileOrderNotice;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@RequestMapping(value = "/fenqile/callback")
@Slf4j
public class FenqileCallbackController {

    @Autowired
    private H5PayServiceImpl h5PayServiceImpl;
    @Autowired
    private FenqileOrderService fenqileOrderService;
    @Qualifier("h5OrderServiceImpl")
    @Autowired
    private H5OrderServiceImpl h5OrderService;

    @RequestMapping(value = "order")
    @ResponseBody
    public String payCallBack(HttpServletRequest request) {
        //{"sign":"45f6ba64d0d2247f468d85242f1cb9f2","amount":0.01,"subject":"绝地求生：刺激战场 1*局","third_order_id":"TEST180926997162","merch_sale_state":10,"order_id":"O20180926620566103844"}
        String result = "";
        try {
            result = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            FenqileOrderNotice fenqileOrderNotice = parseResult(result);

            FenqileOrder origOrder = fenqileOrderService.findByOrderNo(fenqileOrderNotice.getThirdOrderId());
            if (origOrder != null) {
                if (StringUtils.isBlank(origOrder.getFenqileNo())) {
                    //更新分期乐订单数据
                    origOrder.setFenqileNo(fenqileOrderNotice.getOrderId());
                    origOrder.setUpdateTime(new Date());
                    fenqileOrderService.update(origOrder);
                }
            }

            if (Integer.valueOf(12).equals(fenqileOrderNotice.getMerchSaleState())) {
                PayCallbackModel payCallbackModel = PayCallbackModel.newBuilder(PaymentEnum.FENQILE_PAY.getType(), PayBusinessEnum.ORDER).fenqileOrderNotice(fenqileOrderNotice).build();
                if (h5PayServiceImpl.payResult(payCallbackModel)) {
                    return "{\"result\":0}";
                }
            } else if (Integer.valueOf(15).equals(fenqileOrderNotice.getMerchSaleState())) {
                h5OrderService.fenqileUserCancelOrder(fenqileOrderNotice.getThirdOrderId());
                return "{\"result\":0}";
            } else if (Integer.valueOf(10).equals(fenqileOrderNotice.getMerchSaleState())) {
                if (origOrder == null) {
                    throw new OrderException(OrderException.ExceptionCode.ORDER_NOT_EXIST, fenqileOrderNotice.getThirdOrderId());
                }
            }

        } catch (Exception e) {
            log.error("回调报文:{}", result);
            log.error("回调结果异常,异常原因:", e);
        }
        return "{\"result\":1}";
    }


    protected FenqileOrderNotice parseResult(String result) {
        //{"sign":"45f6ba64d0d2247f468d85242f1cb9f2","amount":0.01,"subject":"绝地求生：刺激战场 1*局","third_order_id":"TEST180926997162","merch_sale_state":10,"order_id":"O20180926620566103844"}
        JSONObject jso = JSONObject.parseObject(result);
        FenqileOrderNotice fenqileOrderNotice = BeanUtil.fillBeanWithMap(jso.getInnerMap(), new FenqileOrderNotice(), true, true);
        return fenqileOrderNotice;
    }


}
