package com.fulu.game.app.controller;


import com.fulu.game.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@Slf4j
@RequestMapping("/api/v1/charge")
public class ChargeController extends BaseController {






    /**
     * 余额充值接口
     * @param request
     * @param sessionkey
     * @param money
     * @return
     */
    @RequestMapping(value = "balance")
    public Result balanceCharge(HttpServletRequest request,
                                @RequestParam(required = true) Integer payment,
                                @RequestParam(required = true) String sessionkey,
                                @RequestParam(required = true) BigDecimal money) {
        return Result.success();
    }


}
