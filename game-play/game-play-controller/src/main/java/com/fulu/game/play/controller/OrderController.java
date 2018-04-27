package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 提交订单
     * @param productId
     * @param num
     * @param remark
     * @return
     */
    @RequestMapping(value = "submit")
    public Result submit(Integer productId,
                         Integer num,
                         String remark){
        OrderVO orderVO = orderService.submit(productId,num,remark);
        return Result.success().data(orderVO).msg("创建订单成功!");
    }

    /**
     * 模拟订单支付
     * @param orderNo
     * @return
     */

    @RequestMapping(value = "/test/pay")
    @Deprecated
    public Result payTest(String orderNo){
        OrderVO orderVO =orderService.payOrder(orderNo);
        return Result.success().data(orderVO).msg("订单支付成功!");
    }




    /**
     * 用户取消订单
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/user/cancel")
    public Result userCancelOrder(String orderNo){
        OrderVO orderVO =orderService.userCancelOrder(orderNo);
        return Result.success().data(orderVO).msg("取消订单成功!");
    }

    /**
     * 用户申诉订单
     * @param orderNo
     * @param remark
     * @param fileUrl
     * @return
     */
    @RequestMapping(value = "/user/appeal")
    public Result userAppealOrder(String orderNo,String remark,String[] fileUrl){
        OrderVO orderVO =orderService.userAppealOrder(orderNo,remark,fileUrl);
        return Result.success().data(orderVO).msg("订单申诉成功!");
    }

    /**
     * 陪玩师接收订单
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/receive")
    public Result serverReceiveOrder(String orderNo){
        OrderVO orderVO =orderService.serverReceiveOrder(orderNo);
        return Result.success().data(orderVO);
    }


    /**
     * 用户验收订单
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/user/verify")
    public Result userVerifyOrder(String orderNo){
        OrderVO orderVO =orderService.userVerifyOrder(orderNo);
        return Result.success().data(orderVO);
    }


    /**
     * 陪玩师取消订单
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/cancel")
    public Result serverCancelOrder(String orderNo){
        OrderVO orderVO =orderService.serverCancelOrder(orderNo);
        return Result.success().data(orderVO).msg("取消订单成功!");
    }

    /**
     * 陪玩师提交验收订单
     * @param orderNo
     * @param remark
     * @param fileUrl
     * @return
     */
    @RequestMapping(value = "/server/acceptance")
    public Result serverAcceptanceOrder(String orderNo,String remark,String[] fileUrl){
        OrderVO orderVO =orderService.serverAcceptanceOrder(orderNo,remark,fileUrl);
        return Result.success().data(orderVO).msg("订单申诉成功!");
    }



}
