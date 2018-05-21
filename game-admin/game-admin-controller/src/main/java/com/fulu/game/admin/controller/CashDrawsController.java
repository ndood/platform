package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.CashDraws;
import com.fulu.game.core.entity.vo.CashDrawsVO;
import com.fulu.game.core.service.CashDrawsService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * yanbiao
 * 2018.4.24
 * 管理员-提现申请单处理
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/cashDraws")
public class CashDrawsController extends BaseController{
    @Autowired
    private CashDrawsService cashDrawsService;

     /**
     * 管理员-查看提现申请列表
     * @return
     */
    @PostMapping("/list")
    public Result list(CashDrawsVO cashDrawsVO,
                       @RequestParam("pageNum") Integer pageNum,
                       @RequestParam("pageSize") Integer pageSize){
        PageInfo<CashDraws> cashDrawsList = cashDrawsService.list(cashDrawsVO,pageNum,pageSize);
        return Result.success().data(cashDrawsList).msg("查询列表成功！");
    }

    /**
     * 管理员-打款动作
     * @return
     */
    @PostMapping("/draw")
    public Result draw(@RequestParam("cashId") Integer cashId,
            @RequestParam(name="comment",required = false) String comment){
        CashDraws cashDraws = cashDrawsService.draw(cashId,comment);
        if (null == cashDraws){
            return Result.error().msg("申请单不存在！");
        }
        return Result.success().data(cashDraws).msg("打款成功！");
    }
}

