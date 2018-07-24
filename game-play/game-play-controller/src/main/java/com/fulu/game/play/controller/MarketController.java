package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.MarketOrderVO;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Objects;
import com.xiaoleilu.hutool.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/market")
public class MarketController extends BaseController{

    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserInfoAuthService userInfoAuthService;


    /**
     * 订单状态列表
     * @return
     */
    @PostMapping(value = "order/status")
    public Result orderStatus(){
        Map<Integer, String> map = new LinkedHashMap<>();
        for (OrderStatusGroupEnum groupEnum : OrderStatusGroupEnum.values()) {
            if (groupEnum.getType().equals("MARKET")) {
                map.put(groupEnum.getValue(), groupEnum.getName());
            }
        }
        return Result.success().data(map);
    }

    /**
     * 市场订单接单
     * @param orderNo
     * @return
     */
    @PostMapping(value = "order/receive")
    public Result orderReceive(@RequestParam(required = true)String orderNo){
        Order order = null;
        if(redisOpenService.hasKey(orderNo)){
            Map map = redisOpenService.hget(RedisKeyEnum.MARKET_ORDER.generateKey(orderNo));
            order = BeanUtil.mapToBean(map, Order.class, true);
        }else{
            order = orderService.findByOrderNo(orderNo);
        }
        if(order==null){
            throw new OrderException(OrderException.ExceptionCode.ORDER_NOT_EXIST,order.getOrderNo());
        }
        User user = userService.findById(userService.getCurrentUser().getId());
        if (!UserInfoAuthStatusEnum.VERIFIED.getType().equals(user.getUserInfoAuth()) || !UserStatusEnum.NORMAL.getType().equals(user.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_USER_NOT_VERIFIED,order.getOrderNo());
        }
        List<Integer> techAuthList = userTechAuthService.findUserNormalCategoryIds(user.getId());
        if(!techAuthList.contains(order.getCategoryId())){
            throw new OrderException(OrderException.ExceptionCode.ORDER_USER_NOT_HAS_TECH,order.getOrderNo());
        }
        order = orderService.marketReceiveOrder(order.getOrderNo());
        return Result.success().data(order).msg("接单成功!");
    }


    /**
     * 集市订单详情
     * @return
     */
    @PostMapping(value = "order/details")
    public Result marketOrderDetails(@RequestParam(required = true)String orderNo){
        String remark = "";
        User user = userService.getCurrentUser();
        Order order = orderService.findByOrderNo(orderNo);
        if(!Objects.equal(order.getServiceUserId(),user.getId())){
            remark = "接单者可见";
        }else{
            List<Integer> visibleStatus = Arrays.asList(OrderStatusGroupEnum.MARKET_ORDER_REMARK_VISIBLE.getStatusList());
            if(visibleStatus.contains(order.getStatus())){
                remark = order.getRemark();
            }
        }
        order.setRemark(remark);
        MarketOrderVO marketOrderVO = new MarketOrderVO();
        BeanUtil.copyProperties(order,marketOrderVO);

        Category category = categoryService.findById(order.getCategoryId());
        marketOrderVO.setCategoryName(category.getName());
        marketOrderVO.setCategoryIcon(category.getIcon());
        marketOrderVO.setStatusStr(OrderStatusEnum.getMsgByStatus(order.getStatus()));
        return Result.success().data(marketOrderVO);
    }


    /**
     * 设置推送时间间隔
     * @return
     */
    @PostMapping(value = "setting/pushtime")
    public Result settingPushTimeInterval(@RequestParam(required = true)Float minute){
        userInfoAuthService.settingPushTimeInterval(minute);
        return Result.success().msg("设置推送时间间隔成功!");
    }


    /**
     * 获取用户推送时间间隔
     * @return
     */
    @PostMapping(value = "setting/pushtime/get")
    public Result getPushTimeInterval(){
        User user = userService.getCurrentUser();
        UserInfoAuth userInfoAuth =  userInfoAuthService.findByUserId(user.getId());
        return Result.success().data(userInfoAuth.getPushTimeInterval());
    }


}
