package com.fulu.game.play.controller;


import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Cdk;
import com.fulu.game.core.entity.OrderMarketProduct;
import com.fulu.game.core.service.CdkService;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.play.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Controller
@RequestMapping(value = "/open/v1/")
@Slf4j
public class OpenController extends BaseController{

    @Autowired
    private OrderService orderService;
    @Autowired
    private CdkService cdkService;


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
    public  Result marketCDKOrder(String sessionKey,
                                 String series,
                                 String gameArea,
                                 String rolename,
                                 String mobile,
                                 HttpServletRequest request){
        log.info("CDK开始下单:series:{};sessionKey:{};gameArea:{};rolename:{};mobile:{}",series,sessionKey,gameArea,rolename,mobile);
        //todo 验证sessionKey
        Cdk cdk = cdkService.findBySeries(series);
        if(cdk==null){
            log.error("CDK无效:series:{};sessionKey:{};gameArea:{};rolename:{};mobile:{}",series,sessionKey,gameArea,rolename,mobile);
            return Result.error().msg("无效的CDK");
        }
        if(cdk.getIsUse()){
            log.error("CDK已经被使用:series:{};sessionKey:{};gameArea:{};rolename:{};mobile:{}",series,sessionKey,gameArea,rolename,mobile);
            return Result.error().msg("该CDK已经被使用过,无法重复使用!");
        }
        String ip = RequestUtil.getIpAdrress(request);
        OrderMarketProduct orderMarketProduct = new OrderMarketProduct();
        orderMarketProduct.setPrice(cdk.getPrice());
        orderMarketProduct.setCategoryId(cdk.getCategoryId());
        orderMarketProduct.setRolename(rolename);
        orderMarketProduct.setMobile(mobile);
        orderMarketProduct.setGameArea(gameArea);
        orderMarketProduct.setAmount(1);
        orderMarketProduct.setType(cdk.getType());
        //获取商品名
        StringBuffer productName = new StringBuffer();
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
        log.info("CDK下单:orderMarketProduct:{};channelId:{};remark:{};ip:{}",orderMarketProduct,channelId,remark,ip);
        orderService.submitMarketOrder(channelId,orderMarketProduct,remark.toString(),ip,series);
        return Result.success().msg("下单成功!");
    }




}
