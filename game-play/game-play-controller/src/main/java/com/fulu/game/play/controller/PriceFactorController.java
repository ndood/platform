package com.fulu.game.play.controller;


import com.fulu.game.common.Result;
import com.fulu.game.core.entity.PriceFactor;
import com.fulu.game.core.entity.vo.PriceFactorVO;
import com.fulu.game.core.service.PriceFactorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/factor")
@Slf4j
public class PriceFactorController extends BaseController {

    @Autowired
    private PriceFactorService priceFactorService;


    @PostMapping(value = "/get")
    public Result priceFactor() {
        PriceFactor priceFactor = priceFactorService.findByNewPriceFactor();
        if (priceFactor == null) {
            return Result.success().data(1);
        }
        BigDecimal factor = priceFactor.getFactor();
        String[] categoryIds = priceFactor.getCategoryIds().split(",");
        Map<String, Object> result = new HashMap<>();
        result.put("factor", factor);
        result.put("categoryIds", categoryIds);
        return Result.success().data(result);
    }

    /**
     * 获取CJ首页展示的游戏列表
     *
     * @return 封装结果集
     */
    @PostMapping(value = "/cj/get")
    public Result cjPriceFactor() {
        List<PriceFactorVO> voList = priceFactorService.findCjPriceFactor();
        if (voList == null) {
            return Result.error().data("查询结果为空");
        }
        return Result.success().data(voList);
    }

}
