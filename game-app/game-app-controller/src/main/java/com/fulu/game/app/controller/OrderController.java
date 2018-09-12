package com.fulu.game.app.controller;


import com.fulu.game.app.service.impl.AppOrderServiceImpl;
import com.fulu.game.app.util.RequestUtil;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.DataException;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("/api/v1/order")
public class OrderController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private AppOrderServiceImpl appOrderServiceImpl;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;


    @RequestMapping(value = "submit")
    public Result submit(@RequestParam(required = true) Integer productId,
                         @RequestParam(required = true) Integer num,
                         @RequestParam(required = true) Integer payment,
                         String couponNo,
                         @RequestParam(required = true) String sessionkey,
                         String remark,
                         HttpServletRequest request) {
        User user = userService.getCurrentUser();
        if (!redisOpenService.hasKey(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey))) {
            log.error("验证sessionkey错误:productId:{};num:{};couponNo:{};sessionkey:{};remark:{};userId:{}", productId, num, couponNo, sessionkey, remark, user.getId());
            throw new DataException(DataException.ExceptionCode.NO_FORM_TOKEN_ERROR);
        }
        try {
            String ip = RequestUtil.getIpAdrress(request);
            String orderNo = appOrderServiceImpl.submit(productId, num,payment,remark,couponNo,ip);
            return Result.success().data(orderNo).msg("创建订单成功!");
        } finally {
            redisOpenService.delete(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey));
        }
    }



}
