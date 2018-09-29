package com.fulu.game.h5.controller.fenqile;


import com.fulu.game.play.service.impl.H5FenqilePayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/fenqile/callback")
@Slf4j
public class FenqileCallbackController {

    @Autowired
    private H5FenqilePayServiceImpl h5FenqilePayService;


    @RequestMapping(value = "order")
    @ResponseBody
    public String payCallBack(HttpServletRequest request) {

        //{"sign":"45f6ba64d0d2247f468d85242f1cb9f2","amount":0.01,"subject":"绝地求生：刺激战场 1*局","third_order_id":"TEST180926997162","merch_sale_state":10,"order_id":"O20180926620566103844"}

        try {
            String result = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            log.info("分期乐回调requestbody:{}",result);
            return h5FenqilePayService.payResult(result);
        }catch (Exception e){
            log.error("分期乐回调异常",e);
        }
        return "{\"result\":1}";
    }






}
