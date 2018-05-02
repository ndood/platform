package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.MoneyDetails;
import com.fulu.game.core.entity.vo.MoneyDetailsVO;
import com.fulu.game.core.service.MoneyDetailsService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * 管理员-查询-加零钱列表
     * @param moneyDetailsVO
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping("/list")
    public Result list(@ModelAttribute MoneyDetailsVO moneyDetailsVO,
                       @RequestParam("pageSize") Integer pageSize,
                       @RequestParam("pageNum") Integer pageNum){
        if (StringUtils.isEmpty(moneyDetailsVO.getOrderBy())){
            moneyDetailsVO.setOrderBy("tmd.create_time DESC");
        }
        PageInfo<MoneyDetailsVO> list = moneyDetailsService.listByAdmin(moneyDetailsVO,pageSize,pageNum);
        return Result.success().data(list).msg("查询列表成功！");
    }

    /**
     * 管理员-加零钱
     * @param moneyDetailsVO
     * @return
     */
    @RequestMapping("/save")
    public Result save(@ModelAttribute MoneyDetailsVO moneyDetailsVO){
        MoneyDetails moneyDetails = moneyDetailsService.save(moneyDetailsVO);
        return Result.success().data(moneyDetails).msg("操作成功！");
    }
}
