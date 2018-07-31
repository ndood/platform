package com.fulu.game.point.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.CashDraws;
import com.fulu.game.core.entity.vo.CashDrawsVO;
import com.fulu.game.core.service.CashDrawsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提现申请Controller
 *
 * @author Gong ZeChun
 * @date 2018/7/31 9:56
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/cashDraws")
public class CashDrawsController extends BaseController {
    private final CashDrawsService cashDrawsService;

    @Autowired
    public CashDrawsController(CashDrawsService cashDrawsService) {
        this.cashDrawsService = cashDrawsService;
    }

    /**
     * 生成提现申请记录单
     *
     * @param cashDrawsVO 提现申请VO
     * @return 封装结果集
     */
    @PostMapping("/save")
    public Result save(CashDrawsVO cashDrawsVO) {
        CashDraws cashDraws = cashDrawsService.save(cashDrawsVO);
        return Result.success().data(cashDraws).msg("提现申请成功！");
    }


}

