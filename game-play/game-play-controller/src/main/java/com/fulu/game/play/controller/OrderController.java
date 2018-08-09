package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.common.exception.SystemException;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderDeal;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.OrderDealVO;
import com.fulu.game.core.entity.vo.OrderDetailsVO;
import com.fulu.game.core.entity.vo.OrderEventVO;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.service.OrderDealService;
import com.fulu.game.core.service.OrderEventService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.play.service.impl.PilotOrderServiceImpl;
import com.fulu.game.play.service.impl.PlayMiniAppOrderServiceImpl;
import com.fulu.game.play.utils.RequestUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@Slf4j
@RequestMapping("/api/v1/order")
public class OrderController extends BaseController {

    @Autowired
    private PlayMiniAppOrderServiceImpl orderService;
    @Autowired
    private PilotOrderServiceImpl pilotOrderService;
    @Autowired
    private OrderDealService orderDealService;
    @Autowired
    private PlayMiniAppPayServiceImpl playMiniAppPayService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;


    /**
     * 查询陪玩是否是服务状态
     *
     * @param productId
     * @return
     */
    @RequestMapping(value = "isBusing")
    public Result isBusing(@RequestParam(required = true) Integer productId) {
        return Result.success().msg("陪玩师空闲状态!");
    }


    /**
     * 提交订单
     *
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
        if (!redisOpenService.hasKey(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey))) {
            log.error("验证sessionkey错误:productId:{};num:{};couponNo:{};sessionkey:{};remark:{};userId:{}",
                    productId, num, couponNo, sessionkey, remark, user.getId());
            throw new SystemException(SystemException.ExceptionCode.NO_FORM_TOKEN_ERROR);
        }
        try {
            String ip = RequestUtil.getIpAdrress(request);
            String orderNo = orderService.submit(productId, num, remark, couponNo, ip, contactType, contactInfo);
            return Result.success().data(orderNo).msg("创建订单成功!");
        } finally {
            redisOpenService.delete(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey));
        }
    }

    /**
     * 领航小程序提交订单
     *
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
    @RequestMapping(value = "pilot/submit")
    public Result pilotSubmit(@RequestParam(required = true) Integer productId,
                              HttpServletRequest request,
                              @RequestParam(required = true) Integer num,
                              String couponNo,
                              @RequestParam(required = true) String sessionkey,
                              String remark,
                              Integer contactType,
                              String contactInfo) {
        User user = userService.getCurrentUser();
        if (!redisOpenService.hasKey(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey))) {
            log.error("验证sessionkey错误:productId:{};num:{};couponNo:{};sessionkey:{};remark:{};userId:{}", productId, num, couponNo, sessionkey, remark, user.getId());
            throw new SystemException(SystemException.ExceptionCode.NO_FORM_TOKEN_ERROR);
        }
        String ip = RequestUtil.getIpAdrress(request);
        try {
            String orderNo = pilotOrderService.pilotSubmit(productId, num, remark, couponNo, ip, contactType, contactInfo);
            return Result.success().data(orderNo).msg("创建订单成功!");
        } finally {
            redisOpenService.delete(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey));
        }
    }

    /**
     * 订单支付接口
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/pay")
    @Deprecated
    public Result pay(@RequestParam(required = true) String orderNo,
                      HttpServletRequest request) {
        String ip = RequestUtil.getIpAdrress(request);
        Order order = orderService.findByOrderNo(orderNo);
        User user = userService.findById(order.getUserId());
        Object result = playMiniAppPayService.payOrder(order, user, ip);
        return Result.success().data(result);
    }

    /**
     * 订单列表
     *
     * @param pageNum
     * @param pageSize
     * @param type
     * @return
     */
    @RequestMapping(value = "/list")
    public Result orderList(@RequestParam(required = true) Integer pageNum,
                            @RequestParam(required = true) Integer pageSize,
                            Integer type) {
        PageInfo<OrderDetailsVO> page = orderService.list(pageNum, pageSize, type);
        return Result.success().data(page);
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
     * 申请客服仲裁
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
        orderService.userAppealOrder(orderNo, remark, fileUrl);
        return Result.success().data(orderNo).msg("订单申诉成功!");
    }


    /**
     * 提醒接单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/remind/receive-order")
    public Result remindOrder(@RequestParam(required = true) String orderNo) {
        if (redisOpenService.isTimeIntervalInside(orderNo)) {
            return Result.error().msg("不能频繁提醒接单!");
        }
        Order order = orderService.findByOrderNo(orderNo);
        orderService.pushToServiceOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOSERVICE_REMIND_RECEIVE);
        redisOpenService.setTimeInterval(orderNo, 5 * 60);
        return Result.success().msg("提醒接单成功!");
    }


    /**
     * 提醒开始服务
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/remind/start-order")
    public Result remindStartOrder(@RequestParam(required = true) String orderNo) {
        if (redisOpenService.isTimeIntervalInside(orderNo)) {
            return Result.error().msg("不能频繁提醒开始!");
        }
        Order order = orderService.findByOrderNo(orderNo);
        orderService.pushToServiceOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOSERVICE_REMIND_START_SERVICE);
        redisOpenService.setTimeInterval(orderNo, 5 * 60);
        return Result.success().msg("提醒开始成功!");
    }

    /**
     * 用户协商订单
     *
     * @return
     */
    @RequestMapping(value = "/user/consult")
    public Result consult(@RequestParam(required = true) String orderNo,
                          BigDecimal refundMoney,
                          String remark,
                          @RequestParam(required = true) String[] fileUrl) {

        if (redisOpenService.isTimeIntervalInside(OrderEventService.CANCEL_CONSULT_LIMIT + orderNo)) {
            return Result.error().msg("取消协商后,两个小时内不能重复协商!");
        }
        orderService.userConsultOrder(orderNo, refundMoney, remark, fileUrl);
        return Result.success().data(orderNo);
    }


    /**
     * 陪玩师同意协商
     *
     * @param orderNo
     * @param orderEventId
     * @return
     */
    @RequestMapping(value = "/server/consult-appeal")
    public Result consultAppeal(@RequestParam(required = true) String orderNo,
                                Integer orderEventId) {
        orderService.consultAgreeOrder(orderNo, orderEventId);
        return Result.success().data(orderNo);
    }


    /**
     * 陪玩师拒绝协商
     *
     * @param orderNo
     * @param orderEventId
     * @return
     */
    @RequestMapping(value = "/server/consult-reject")
    public Result consultReject(@RequestParam(required = true) String orderNo,
                                Integer orderEventId,
                                String remark,
                                @RequestParam(required = true) String[] fileUrl) {
        orderService.consultRejectOrder(orderNo, orderEventId, remark, fileUrl);
        return Result.success().data(orderNo);
    }

    /**
     * 用户取消协商
     *
     * @param orderNo
     * @param orderEventId
     * @return
     */
    @RequestMapping(value = "/user/consult-cancel")
    public Result consultCancel(@RequestParam(required = true) String orderNo,
                                Integer orderEventId) {
        orderService.consultCancelOrder(orderNo, orderEventId);
        return Result.success().data(orderNo).msg("取消协商成功!");
    }

    /**
     * 陪玩师接收订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/receive")
    public Result serverReceiveOrder(@RequestParam String orderNo,
                                     String version) {
        orderService.serverReceiveOrder(orderNo);
        return Result.success().data(orderNo).msg("接单成功!");
    }

    /**
     * 陪玩师开始服务
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/start-serve")
    public Result startServerOrder(@RequestParam(required = true) String orderNo) {
        orderService.serverStartServeOrder(orderNo);
        return Result.success().data(orderNo).msg("接单成功!");
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
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/acceptance")
    public Result serverAcceptanceOrder(@RequestParam(required = true) String orderNo,
                                        String remark,
                                        String[] fileUrl) {
        OrderVO orderVO = orderService.serverAcceptanceOrder(orderNo, remark, fileUrl);
        return Result.success().data(orderVO).msg("提交订单验收成功!");
    }


    /**
     * 订单事件查询（查询协商和申诉）
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/event")
    public Result orderEvent(@RequestParam(required = true) String orderNo) {
        OrderEventVO orderEventVO = orderService.findOrderEvent(orderNo);
        return Result.success().data(orderEventVO);
    }


    @RequestMapping(value = "/leave-msg")
    public Result orderLeaveMsg(@RequestParam(required = true) String orderNo,
                                Integer orderEventId,
                                String remark,
                                String[] fileUrl) {
        OrderDeal orderDeal = orderService.eventLeaveMessage(orderNo, orderEventId, remark, fileUrl);
        return Result.success().data(orderDeal);
    }


    /**
     * 查看申诉或者验收截图
     *
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
     * 订单详情
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/details")
    public Result orderDetails(@RequestParam(required = true) String orderNo) {
        OrderDetailsVO orderDetailsVO = orderService.findOrderDetails(orderNo);
        return Result.success().data(orderDetailsVO);
    }


}
