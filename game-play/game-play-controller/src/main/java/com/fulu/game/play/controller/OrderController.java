package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "submit")
    public Result submit(Integer productId,
                         Integer num,
                         String remark){
        OrderVO orderVO = orderService.submit(productId,num,remark);
        return Result.success().data(orderVO);
    }



}
