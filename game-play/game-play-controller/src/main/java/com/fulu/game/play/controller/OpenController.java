package com.fulu.game.play.controller;


import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Cdk;
import com.fulu.game.core.entity.OrderMarketProduct;
import com.fulu.game.core.service.CdkService;
import com.fulu.game.core.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Controller
@RequestMapping(value = "/open")
@Slf4j
public class OpenController extends BaseController{

    @Autowired
    private OrderService orderService;
    @Autowired
    private CdkService cdkService;



    @RequestMapping(value = "/cdk/order")
    public Result marketCDKOrder(String sessionKey,
                                 String series,
                                 String gameArea,
                                 String rolename,
                                 String mobile){
        //todo 验证sessionKey
        Cdk cdk = cdkService.findBySeries(series);
        if(cdk==null){
            return Result.error().msg("无效的CDK");
        }
        if(cdk.getIsUse()){
            return Result.error().msg("该CDK已经被使用过,无法重复使用!");
        }
        OrderMarketProduct orderMarketProduct = new OrderMarketProduct();
        BigDecimal price = cdk.getPrice();
        Integer categoryId =  cdk.getCategoryId();
        String type =  cdk.getType();


        return Result.success();
    }


}
