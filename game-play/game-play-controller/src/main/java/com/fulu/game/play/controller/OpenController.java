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
import java.math.BigDecimal;
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
     * web页面CDK下单
     * @param sessionKey
     * @param series
     * @param gameArea
     * @param rolename
     * @param mobile
     * @param request
     * @return
     */
    @PostMapping(value = "/cdk/order")
    @ResponseBody
    public  Result marketCDKOrder(@RequestParam(required = true)String sessionKey,
                                  @RequestParam(required = true)String series,
                                  String gameArea,
                                  @RequestParam(required = true)String rolename,
                                  @RequestParam(required = true)String mobile,
                                  HttpServletRequest request){

        if(StringUtils.isBlank(series)){
            return Result.error().msg("cdk不能为空!");
        }

        String ip = RequestUtil.getIpAdrress(request);
        log.info("CDK开始下单:series:{};sessionKey:{};gameArea:{};rolename:{};mobile:{};ip:{};",series,sessionKey,gameArea,rolename,mobile,ip);
        //验证sessionKey
        if(!redisOpenService.hasKey(RedisKeyEnum.WRITER_SESSION_KEY.generateKey(sessionKey))){
            log.error("sessionKey不匹配");
            return Result.error().msg("页面停留时间过长,请刷新后再尝试!");
        }
        String storeSeries = redisOpenService.get(RedisKeyEnum.WRITER_SESSION_KEY.generateKey(sessionKey));
        if(!Objects.equal(storeSeries,series)){
            return Result.error().msg("页面停留时间过长,请刷新后再尝试!");
        }
        //验证角色名和手机号
        if(StringUtils.isBlank(rolename)||StringUtils.isBlank(mobile)){
            return Result.error().msg("角色名和手机号不能为空!");
        }
        //验证cdk
        Cdk cdk = cdkService.findBySeries(series);
        if(!cdk.getEnable()){
            return Result.error().msg("该CDK已过期,无法使用!");
        }
        if(cdk==null){
            log.error("CDK无效:series:{};sessionKey:{};gameArea:{};rolename:{};mobile:{};ip:{};",series,sessionKey,gameArea,rolename,mobile,ip);
            return Result.error().msg("无效的CDK");
        }
        if(cdk.getIsUse()){
            log.error("CDK已经被使用:series:{};sessionKey:{};gameArea:{};rolename:{};mobile:{};ip:{};",series,sessionKey,gameArea,rolename,mobile,ip);
            return Result.error().msg("该CDK已经被使用过,无法重复使用!");
        }
        OrderMarketProduct orderMarketProduct = new OrderMarketProduct();
        orderMarketProduct.setPrice(cdk.getPrice());
        orderMarketProduct.setCategoryId(cdk.getCategoryId());
        orderMarketProduct.setRolename(rolename);
        orderMarketProduct.setMobile(mobile);
        orderMarketProduct.setGameArea(gameArea);
        orderMarketProduct.setAmount(1);
        orderMarketProduct.setType(cdk.getType());
        //获取商品名
        StringBuilder productName = new StringBuilder();
        productName.append(cdk.getType()).append("|");
        if(StringUtils.isNotBlank(gameArea)){
            productName.append(gameArea).append("|");
        }
        productName.append("5分钟上线");
        orderMarketProduct.setProductName(productName.toString());
        //获取备注
        StringBuffer remark = new StringBuffer();
        remark.append("手机号码:").append(mobile).append(";");
        remark.append("角色名:").append(rolename);
        Integer channelId = cdk.getChannelId();
        log.info("CDK执行下单:orderMarketProduct:{};channelId:{};remark:{};ip:{}",orderMarketProduct,channelId,remark,ip);
        orderService.submitMarketOrder(channelId,orderMarketProduct,remark.toString(),ip,series);
        //删除sessionKey
        redisOpenService.delete(RedisKeyEnum.WRITER_SESSION_KEY.generateKey(sessionKey));
        return Result.success().msg("下单成功!");
    }


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
        return Result.success().data(orderMarketProductVO);
    }




}
