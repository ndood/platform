package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.core.entity.vo.MarketOrderVO;
import com.fulu.game.core.service.OrderService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/market")
public class MarketController extends BaseController{

    @Autowired
    private OrderService orderService;



    @PostMapping(value = "order/list")
    public Result orderList(Integer pageNum,
                            Integer pageSize,
                            Integer categoryId,
                            Integer status){
        if (status == null) {
            status = OrderStatusGroupEnum.MARKET_ALL.getValue();
        }
        Integer[] statusArr = OrderStatusGroupEnum.getByValue(status);
        PageInfo<MarketOrderVO> list = orderService.marketList(pageNum,pageSize,categoryId,statusArr);
        return Result.success().data(list);
    }







}
