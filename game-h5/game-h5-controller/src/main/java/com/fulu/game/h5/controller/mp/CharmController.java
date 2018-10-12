package com.fulu.game.h5.controller.mp;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.CashDrawsVO;
import com.fulu.game.core.service.CashDrawsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 魅力值Controller
 *
 * @author Gong ZeChun
 * @date 2018/10/9 10:08
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/charm")
public class CharmController {
    @Autowired
    private CashDrawsService cashDrawsService;

    /**
     * 魅力值提现
     *
     * @param charm 魅力值
     * @return 封装结果集
     */
    @PostMapping("/withdraw")
    public Result withdrawCharm(@RequestParam Integer charm) {
        CashDrawsVO vo = cashDrawsService.withdrawCharm(charm);
        return Result.success().data(vo).msg("魅力值提现申请成功！");
    }
}
