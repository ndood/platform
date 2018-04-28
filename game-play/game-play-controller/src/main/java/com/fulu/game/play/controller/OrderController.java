package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.ProductService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;

    /**
     * 查询陪玩是否是服务状态
     * @param productId
     * @return
     */
    @RequestMapping(value = "canservice")
    public Result canService(Integer productId){
        Product product = productService.findById(productId);
        Boolean status = orderService.isAlreadyService(product.getUserId());
        if(status){
            return Result.error().msg("当前陪玩师正在服务中,需要等待后才能为您提供服务!");
        }
        return Result.success().msg("陪玩师空闲状态!");
    }

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
        return Result.success().data(orderVO.getOrderNo()).msg("创建订单成功!");
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
     * 用户订单状态列表
     * @return
     */
    @RequestMapping(value = "/user/status")
    public Result userOrderStatus(){
        Map<String,Integer> map = new LinkedHashMap<>();
        for(OrderStatusGroupEnum groupEnum : OrderStatusGroupEnum.values()){
            if(groupEnum.getType().equals("USER")){
                map.put(groupEnum.getName(),groupEnum.getValue());
            }
        }
        return Result.success().data(map);
    }

    /**
     * 用户订单列表
     * @return
     */
    @RequestMapping(value = "/user/list")
    public Result userOrderList(Integer pageNum,
                                Integer pageSize,
                                Integer categoryId,
                                Integer status){
        Integer[] statusArr = OrderStatusGroupEnum.getByValue(status);
        PageInfo<OrderVO> pageInfo = orderService.userList(pageNum,pageSize,categoryId,statusArr);
        return Result.success().data(pageInfo);
    }

    /**
     * 打手订单状态列表
     * @return
     */
    @RequestMapping(value = "/server/status")
    public Result serverOrderStatus(){
        Map<String,Integer> map = new LinkedHashMap<>();
        for(OrderStatusGroupEnum groupEnum : OrderStatusGroupEnum.values()){
            if(groupEnum.getType().equals("SERVER")){
                map.put(groupEnum.getName(),groupEnum.getValue());
            }
        }
        return Result.success().data(map);
    }

    /**
     * 打手订单列表
     * @return
     */
    @RequestMapping(value = "/server/list")
    public Result serverOrderList(Integer pageNum,
                                Integer pageSize,
                                Integer categoryId,
                                Integer status){
        Integer[] statusArr = OrderStatusGroupEnum.getByValue(status);
        PageInfo<OrderVO> pageInfo = orderService.serverList(pageNum,pageSize,categoryId,statusArr);
        return Result.success().data(pageInfo);
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
        return Result.success().data(orderVO).msg("接单成功!");
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
