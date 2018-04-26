package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.MoneyDetails;
import com.fulu.game.core.entity.vo.MoneyDetailsVO;
import com.fulu.game.core.service.MoneyDetailsService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/moneyDetails")
public class MoneyDetailsController {

    @Autowired
    private MoneyDetailsService moneyDetailsService;

    @RequestMapping("/list")
    public Result list(@ModelAttribute MoneyDetailsVO moneyDetailsVO,
                       @RequestParam("pageSize") Integer pageSize,
                       @RequestParam("pageNum") Integer pageNum){
        PageInfo<MoneyDetailsVO> list = moneyDetailsService.listByUser(moneyDetailsVO,pageSize,pageNum);
        return Result.success().data(list).msg("查询列表成功！");
    }
}
