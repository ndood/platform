package com.fulu.game.h5.controller.fenqile;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@Controller
@RequestMapping(value = "/fenqile/callback")
@Slf4j
public class FenqileCallbackController {


    @RequestMapping(value = "order")
    @ResponseBody
    public String payCallBack(String third_order_id,
                              String order_id,
                              String merch_sale_state,
                              String sign,
                              BigDecimal amount,
                              String subject,
                              String body,
                              String attach) {
        log.info("third_order_id:{},order_id:{},merch_sale_state:{},sign:{},amount:{},subject:{},body:{},attach:{}",
                third_order_id,order_id,merch_sale_state,sign,amount,subject,body,attach);

        return "";
    }

}
