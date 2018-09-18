package com.fulu.game.app.controller;


import com.fulu.game.app.util.RequestUtil;
import com.fulu.game.common.Result;
import com.fulu.game.core.service.impl.VirtualPayOrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@Slf4j
@RequestMapping("/api/v1/charge")
public class ChargeController extends BaseController {


    @Autowired
    private VirtualPayOrderServiceImpl virtualPayOrderService;



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


    /**
     * 提交余额充值订单
     *
     * @param request    request
     * @param sessionkey 订单校验令牌
     * @param money      充值到平台的金额
     * @return 封装结果集
     */
    @PostMapping("/balance")
    public Result balanceCharge(HttpServletRequest request,
                                @RequestParam String sessionkey,
                                @RequestParam BigDecimal money) {
        String ip = RequestUtil.getIpAdrress(request);
        return Result.success().msg("创建订单成功!");
    }


}
