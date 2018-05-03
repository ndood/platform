package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.CashDraws;
import com.fulu.game.core.entity.vo.CashDrawsVO;
import com.fulu.game.core.service.CashDrawsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * yanbiao
 * 2018.4.24
 * 提现申请记录单业务处理类
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/cashDraws")
public class CashDrawsController extends BaseController{
    @Autowired
    private CashDrawsService cashDrawsService;

    /**
     * 生成提现申请记录单
     * @param cashDrawsVO
     * @return
     */
    @RequestMapping("/save")
    public Result save(CashDrawsVO cashDrawsVO){
        CashDraws cashDraws = cashDrawsService.save(cashDrawsVO);
        return Result.success().data(cashDraws).msg("提现申请成功！");
    }

}

