package com.fulu.game.play.controller;


import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.entity.Cdk;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderMarketProduct;
import com.fulu.game.core.entity.vo.OrderMarketProductVO;
import com.fulu.game.core.service.CdkService;
import com.fulu.game.core.service.OrderMarketProductService;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.play.utils.RequestUtil;
import com.google.common.base.Objects;
import com.xiaoleilu.hutool.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/open/v1/")
@Slf4j
public class OpenController extends BaseController{

    @Autowired
    private OrderService orderService;
    @Autowired
    private CdkService cdkService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private OrderMarketProductService orderMarketProductService;




    /**
     * 通过CDK查询游戏类型
     * @param series
     * @return
     */
    @PostMapping(value = "/cdk/type")
    @ResponseBody
    public Result findTypeByCdKey(@RequestParam(required = true) String series){
        Cdk cdk = cdkService.findBySeries(series);
        //todo cdk废除提醒
        if(cdk==null){
            return Result.error().msg("无效的CDK");
        }
        if(!cdk.getEnable()){
            return Result.error().msg("该CDK已过期,无法使用!");
        }
        if(cdk.getIsUse()){
            return Result.error().msg("该CDK已经被使用过,无法重复使用!");
        }
        String token = GenIdUtil.GetGUID();
        redisOpenService.set(RedisKeyEnum.WRITER_SESSION_KEY.generateKey(token),cdk.getSeries(), Constant.TIME_HOUR_TWO);
        Map<String,String> data = new HashMap<>();
        data.put("type",cdk.getType());
        data.put("sessionKey",token);
        return Result.success().data(data);
    }

    /**
     * CDK状态查询
     * @param series
     * @return
     */
    @PostMapping(value = "/cdk/query")
    @ResponseBody
    public Result cdKeyQuery(@RequestParam(required = true) String series){
        Cdk cdk = cdkService.findBySeries(series);
        if(cdk==null){
            return Result.error().msg("无效的CDK");
        }
        if(!cdk.getIsUse()||cdk.getOrderNo()==null){
            return Result.error().msg("该CDK还未被使用,无法查询!");
        }
        OrderMarketProduct orderMarketProduct =  orderMarketProductService.findByOrderNo(cdk.getOrderNo());
        Order order = orderService.findByOrderNo(cdk.getOrderNo());
        OrderMarketProductVO orderMarketProductVO = new OrderMarketProductVO();
        BeanUtil.copyProperties(orderMarketProduct,orderMarketProductVO);
        orderMarketProductVO.setSeries(series);
        orderMarketProductVO.setStatusStr(OrderStatusEnum.getMsgByStatus(order.getStatus()));
        orderMarketProductVO.setPrice(null);
        orderMarketProductVO.setAmount(null);
        return Result.success().data(orderMarketProductVO);
    }




}
