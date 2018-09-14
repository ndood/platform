package com.fulu.game.app.controller;

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
}
