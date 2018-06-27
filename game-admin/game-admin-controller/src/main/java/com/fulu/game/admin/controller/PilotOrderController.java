package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.searchVO.OrderSearchVO;
import com.fulu.game.core.service.PilotOrderService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/api/v1/pilotorder")
public class PilotOrderController extends BaseController {

    @Autowired
    private PilotOrderService pilotOrderService;


    @RequestMapping("/list")
    public Result list(@RequestParam(required = true) Integer pageNum,
                       @RequestParam(required = true) Integer pageSize,
                       String orderBy,
                       OrderSearchVO orderSearchVO) {
        PageInfo pageInfo = pilotOrderService.findVoList(pageNum,pageSize,orderBy,orderSearchVO);
        return Result.success().data(pageInfo).msg("查询列表成功！");
    }


    @RequestMapping("/amount")
    public Result amountOfProfit(Date startTime,
                                 Date endTime){
        BigDecimal amount = pilotOrderService.amountOfProfit(startTime,endTime);
        return Result.success().data(amount);
    }


}
