package com.fulu.game.play.controller;


import com.fulu.game.common.Result;
import com.fulu.game.core.entity.PriceFactor;
import com.fulu.game.core.service.PriceFactorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/api/v1/factor")
@Slf4j
public class PriceFactorController extends BaseController{

    @Autowired
    private PriceFactorService priceFactorService;


    @PostMapping(value = "/get")
    public Result priceFactor(){
        PriceFactor priceFactor = priceFactorService.findByNewPriceFactor();
        if(priceFactor==null){
            return Result.success().data(1);
        }
        return Result.success().data(priceFactor.getFactor());
    }


}
