package com.fulu.game.app.controller;

import com.fulu.game.app.service.impl.AppOrderPayServiceImpl;
import com.fulu.game.common.enums.PaymentEnum;
import com.fulu.game.core.entity.payment.model.PayCallbackModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping(value = "alipay")
@Slf4j
public class AlipayController {


    @Autowired
    private AppOrderPayServiceImpl appOrderPayService;


    /**
     * 订单业务回调
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/pay/order/callback")
    public String payOrderNotify(HttpServletRequest request) {
        Map<String, String> params = getAlipayParams(request.getParameterMap());
        PayCallbackModel payCallbackModel = PayCallbackModel.newBuilder(PaymentEnum.ALIPAY_PAY.getType()).aliPayParameterMap(params).build();
        if (appOrderPayService.payResult(payCallbackModel)) {
            return "success";
        }
        return "fail";
    }

    /**
     * 虚拟充值回调
     * @return
     */
    @ResponseBody
    @RequestMapping("/pay/virtualproduct/callback")
    public String virtualProductOrderNotify() {

        return null;
    }


    public Map<String, String> getAlipayParams(Map requestParams) {
        Map<String, String> params = new HashMap<String, String>();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        return params;
    }

}
