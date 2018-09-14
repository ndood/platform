package com.fulu.game.app.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.VirtualMoneyPriceEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 虚拟币（钻石）Controller
 *
 * @author Gong ZeChun
 * @date 2018/9/14 10:23
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/virtual-money")
public class VirtualMoneyController extends BaseController {

    @PostMapping("/list")
    public Result list() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (VirtualMoneyPriceEnum priceEnum : VirtualMoneyPriceEnum.values()) {
            Map<String, Object> resultMap = new LinkedHashMap<>(16);
            resultMap.put("number", priceEnum.getNumber());
            resultMap.put("priceStr", priceEnum.getPriceStr());
            resultList.add(resultMap);
        }
        return Result.success().data(resultList).msg("查询成功！");
    }


    /**
     * 购买钻石
     *
     * @return
     */
    @PostMapping("/ios-pay")
    public Result diamondPay(String receiptData) {
        HttpRequest httpRequest = HttpUtil.createPost("https://sandbox.itunes.apple.com/verifyReceipt");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("receipt-data", receiptData);
        httpRequest.body(jsonObject.toJSONString(), "application/json; charset=utf-8");
        HttpResponse response = httpRequest.execute();
        if (response.getStatus() == 200) {
            log.info("请求状态200苹果支付结果为:{}", response.body());
            JSONObject jsores = JSONObject.parseObject(response.body());
            if (jsores.get("status").equals(0)){
                return Result.success().msg("支付成功!");
            }
        }
        log.info("请求状态出错:{}", response.body());
        return Result.error().msg("支付失败!");
    }



}



