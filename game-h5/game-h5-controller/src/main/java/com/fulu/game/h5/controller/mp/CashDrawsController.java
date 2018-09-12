package com.fulu.game.h5.controller.mp;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.CashDraws;
import com.fulu.game.core.entity.vo.CashDrawsVO;
import com.fulu.game.core.service.CashDrawsService;
import com.fulu.game.h5.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * yanbiao
 * 2018.4.24
 * 提现申请记录单业务处理类
 */
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class CashDrawsController extends BaseController {
    @Autowired
    private CashDrawsService cashDrawsService;

    /**
     * 生成提现申请记录单
     *
     * @param cashDrawsVO
     * @return
     */
    @PostMapping("/cashDraws/save")
    public Result save(CashDrawsVO cashDrawsVO) {
        CashDraws cashDraws = cashDrawsService.save(cashDrawsVO);
        return Result.success().data(cashDraws).msg("提现申请成功！");
    }

    /**
     * 魅力值提现
     *
     * @param charm 魅力值
     * @return 封装结果集
     */
    @PostMapping("/charm/withdraw")
    public Result withdrawCharm(@RequestParam Integer charm) {
        CashDrawsVO vo = cashDrawsService.withdrawCharm(charm);
        return Result.success().data(vo).msg("魅力值提现申请成功！");
    }
}

