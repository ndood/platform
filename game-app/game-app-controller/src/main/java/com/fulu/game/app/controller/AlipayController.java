package com.fulu.game.app.controller;

import com.alipay.api.internal.util.AlipaySignature;
import lombok.extern.slf4j.Slf4j;
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



    /**
     * 支付宝支付回调地址
     * http://t-api-app.wzpeilian.com/alipay/pay/callback
     * @return
     */
    @ResponseBody
    @RequestMapping("/pay/order/callback")
    public String payOrderNotify(HttpServletRequest request){
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
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
//切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
//boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
//        boolean flag = AlipaySignature.rsaCheckV1(params, alipaypublicKey, charset,"RSA2");


        return null;
    }

    @ResponseBody
    @RequestMapping("/pay/virtualproduct/callback")
    public String virtualProductOrderNotify(){

        return null;
    }


}
