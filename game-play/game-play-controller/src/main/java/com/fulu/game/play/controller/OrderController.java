package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.UserScoreEnum;
import com.fulu.game.common.exception.SystemException;
import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.OrderDealVO;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.play.aop.UserScore;
import com.fulu.game.play.utils.RequestUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderDealService orderDealService;
    @Autowired
    private PayService payService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    /**
     * 查询陪玩是否是服务状态
     * @param productId
     * @return
     */
    @RequestMapping(value = "canservice")
    public Result canService(@RequestParam(required = true) Integer productId) {
        Product product = productService.findById(productId);
        Boolean status = orderService.isAlreadyService(product.getUserId());
        if (status) {
            return Result.error().msg("当前陪玩师正在服务中,需要等待后才能为您提供服务!");
        }
        return Result.success().msg("陪玩师空闲状态!");
    }

    /**
     * 提交订单
     * @param productId
     * @param request
     * @param num
     * @param couponNo
     * @param sessionkey
     * @param remark
     * @param contactType
     * @param contactInfo
     * @return
     */
    @RequestMapping(value = "submit")
    public Result submit(@RequestParam(required = true) Integer productId,
                         HttpServletRequest request,
                         @RequestParam(required = true) Integer num,
                         String couponNo,
                         @RequestParam(required = true) String sessionkey,
                         String remark,
                         Integer contactType,
                         String contactInfo) {
        User user = userService.getCurrentUser();
        if(!redisOpenService.hasKey(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey))){
            log.error("验证sessionkey错误:productId:{};num:{};couponNo:{};sessionkey:{};remark:{};userId:{}",productId,num,couponNo,sessionkey,remark,user.getId());
            throw new SystemException(SystemException.ExceptionCode.NO_FORM_TOKEN_ERROR);
        }
        try {
            String ip = RequestUtil.getIpAdrress(request);
            OrderVO orderVO = orderService.submit(productId, num, remark, couponNo, ip, contactType, contactInfo);
            return Result.success().data(orderVO.getOrderNo()).msg("创建订单成功!");
        }finally {
            redisOpenService.delete(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey));
        }
    }



    @RequestMapping(value = "pilot/submit")
    public Result pilotSubmit(@RequestParam(required = true) Integer productId,
                              HttpServletRequest request,
                              @RequestParam(required = true) Integer num,
                              String couponNo,
                              @RequestParam(required = true) String sessionkey,
                              String remark,
                              Integer contactType,
                              String contactInfo){
        User user = userService.getCurrentUser();
        if(!redisOpenService.hasKey(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey))){
            log.error("验证sessionkey错误:productId:{};num:{};couponNo:{};sessionkey:{};remark:{};userId:{}",productId,num,couponNo,sessionkey,remark,user.getId());
            throw new SystemException(SystemException.ExceptionCode.NO_FORM_TOKEN_ERROR);
        };
        String ip = RequestUtil.getIpAdrress(request);
        try {
            String  orderNum = orderService.pilotSubmit(productId, num, remark, couponNo, ip, contactType, contactInfo);
            return Result.success().data(orderNum).msg("创建订单成功!");
        }finally {
            redisOpenService.delete(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey));
        }
    }


    /**
     * 订单支付接口
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/pay")
    @Deprecated
    public Result pay(@RequestParam(required = true) String orderNo,
                      HttpServletRequest request) {
        String ip = RequestUtil.getIpAdrress(request);
        Object result = payService.wechatUnifyOrder(orderNo, ip);
        return Result.success().data(result);
    }

    /**
     * 用户订单状态列表
     *
     * @return
     */
    @RequestMapping(value = "/user/status")
    public Result userOrderStatus() {
        Map<Integer, String> map = new LinkedHashMap<>();
        for (OrderStatusGroupEnum groupEnum : OrderStatusGroupEnum.values()) {
            if (groupEnum.getType().equals("USER")) {
                map.put(groupEnum.getValue(), groupEnum.getName());
            }
        }
        return Result.success().data(map);
    }

    /**
     * 用户订单列表
     *
     * @return
     */
    @RequestMapping(value = "/user/list")
    public Result userOrderList(@RequestParam(required = true) Integer pageNum,
                                @RequestParam(required = true) Integer pageSize,
                                Integer categoryId,
                                @RequestParam(required = true) Integer status) {
        if (status == null) {
            status = OrderStatusGroupEnum.USER_ALL.getValue();
        }
        Integer[] statusArr = OrderStatusGroupEnum.getByValue(status);
        PageInfo<OrderVO> pageInfo = orderService.userList(pageNum, pageSize, categoryId, statusArr);
        return Result.success().data(pageInfo);
    }

    /**
     * 打手订单状态列表
     *
     * @return
     */
    @RequestMapping(value = "/server/status")
    public Result serverOrderStatus() {
        Map<Integer, String> map = new LinkedHashMap<>();
        for (OrderStatusGroupEnum groupEnum : OrderStatusGroupEnum.values()) {
            if (groupEnum.getType().equals("SERVER")) {
                map.put(groupEnum.getValue(), groupEnum.getName());
            }
        }
        return Result.success().data(map);
    }

    /**
     * 打手订单列表
     *
     * @return
     */
    @RequestMapping(value = "/server/list")
    public Result serverOrderList(@RequestParam(required = true) Integer pageNum,
                                  @RequestParam(required = true) Integer pageSize,
                                  Integer categoryId,
                                  @RequestParam(required = true) Integer status) {
        if (status == null) {
            status = OrderStatusGroupEnum.SERVER_ALL.getValue();
        }
        Integer[] statusArr = OrderStatusGroupEnum.getByValue(status);
        PageInfo<OrderVO> pageInfo = orderService.serverList(pageNum, pageSize, categoryId, statusArr);
        return Result.success().data(pageInfo);
    }

    /**
     * 用户取消订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/user/cancel")
    public Result userCancelOrder(@RequestParam(required = true) String orderNo) {
        OrderVO orderVO = orderService.userCancelOrder(orderNo);
        return Result.success().data(orderVO).msg("取消订单成功!");
    }

    /**
     * 用户申诉订单
     *
     * @param orderNo
     * @param remark
     * @param fileUrl
     * @return
     */
    @RequestMapping(value = "/user/appeal")
    public Result userAppealOrder(@RequestParam(required = true) String orderNo,
                                  String remark,
                                  @RequestParam(required = true) String[] fileUrl) {
        OrderVO orderVO = orderService.userAppealOrder(orderNo, remark, fileUrl);
        return Result.success().data(orderVO).msg("订单申诉成功!");
    }

    /**
     * 陪玩师接收订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/receive")
    @UserScore(type = UserScoreEnum.ACCEPT_ORDER)
    public Result serverReceiveOrder(@RequestParam(required = true) String orderNo) {
        OrderVO orderVO = orderService.serverReceiveOrder(orderNo);
        return Result.success().data(orderVO).msg("接单成功!");
    }


    /**
     * 用户验收订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/user/verify")
    public Result userVerifyOrder(@RequestParam(required = true) String orderNo) {
        OrderVO orderVO = orderService.userVerifyOrder(orderNo);
        return Result.success().data(orderVO).msg("订单验收成功!");
    }


    /**
     * 陪玩师取消订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/cancel")
    public Result serverCancelOrder(@RequestParam(required = true) String orderNo) {
        OrderVO orderVO = orderService.serverCancelOrder(orderNo);
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
    public Result serverAcceptanceOrder(@RequestParam(required = true) String orderNo,
                                        String remark,
                                        @RequestParam(required = true) String[] fileUrl) {
        OrderVO orderVO = orderService.serverAcceptanceOrder(orderNo, remark, fileUrl);
        return Result.success().data(orderVO).msg("提交订单验收成功!");
    }


    /**
     * 查看申诉或者验收截图
     * @return
     */
    @RequestMapping(value = "/deals")
    public Result orderDealList(@RequestParam(required = true) String orderNo) {
        User user = userService.getCurrentUser();
        OrderDealVO orderDealVO = orderDealService.findByUserAndOrderNo(user.getId(), orderNo);
        return Result.success().data(orderDealVO);
    }

    /**
     * 查看陪玩师的验收截图
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/user/acceptance/result")
    public Result userCheckServerAcceptanceResult(@RequestParam(required = true) String orderNo) {
        OrderDealVO orderDealVO = orderDealService.findOrderAcceptanceResult(orderNo);
        return Result.success().data(orderDealVO);
    }

    /**
     * 用户订单详情页
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/user/details")
    public Result userOrderDetails(@RequestParam(required = true) String orderNo) {
        OrderVO orderVO = orderService.findUserOrderDetails(orderNo);
        return Result.success().data(orderVO);
    }


    /**
     * 陪玩师订单详情页
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/details")
    public Result serverOrderDetails(@RequestParam(required = true) String orderNo) {
        OrderVO orderVO = orderService.findServerOrderDetails(orderNo);
        return Result.success().data(orderVO);
    }

}
