package com.fulu.game.h5.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.DataException;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderDeal;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.payment.model.PayRequestModel;
import com.fulu.game.core.entity.payment.res.PayRequestRes;
import com.fulu.game.core.entity.vo.OrderDealVO;
import com.fulu.game.core.entity.vo.OrderDetailsVO;
import com.fulu.game.core.entity.vo.OrderEventVO;
import com.fulu.game.core.service.OrderDealService;
import com.fulu.game.core.service.OrderEventService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.h5.service.impl.H5OrderServiceImpl;
import com.fulu.game.h5.service.impl.H5PayServiceImpl;
import com.fulu.game.h5.utils.RequestUtil;
import com.fulu.game.play.service.impl.PlayMiniAppPushServiceImpl;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * 订单Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/13 17:54
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/order")
public class OrderController extends BaseController {

    private final UserService userService;
    private final RedisOpenServiceImpl redisOpenService;
    private final H5OrderServiceImpl orderService;
    private final OrderDealService orderDealService;
    private final H5PayServiceImpl h5PayService;
    private final PlayMiniAppPushServiceImpl playMiniAppPushService;


    @Autowired
    public OrderController(UserService userService,
                           RedisOpenServiceImpl redisOpenService,
                           OrderDealService orderDealService,
                           H5OrderServiceImpl orderService,
                           H5PayServiceImpl h5PayService,
                           PlayMiniAppPushServiceImpl playMiniAppPushService) {
        this.userService = userService;
        this.redisOpenService = redisOpenService;
        this.orderService = orderService;
        this.orderDealService = orderDealService;
        this.h5PayService = h5PayService;
        this.playMiniAppPushService = playMiniAppPushService;
    }

    /**
     * 提交订单
     *
     * @param productId   商品id
     * @param request     请求
     * @param num         订单数量
     * @param couponNo    优惠券编码
     * @param sessionkey  sessionkey（相当于下单的令牌）
     * @param contactType 联系方式类型(1：手机号；2：QQ号；3：微信号)
     * @param contactInfo 联系方式
     * @return 封装结果集
     */
    @RequestMapping(value = "submit")
    public Result submit(@RequestParam Integer productId,
                         HttpServletRequest request,
                         @RequestParam Integer num,
                         String couponNo,
                         @RequestParam String sessionkey,
                         Integer contactType,
                         String contactInfo) {
        User user = userService.getCurrentUser();
        if (!redisOpenService.hasKey(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey))) {
            log.error("验证sessionkey错误:productId:{};num:{};couponNo:{};sessionkey:{};userId:{}",
                    productId, num, couponNo, sessionkey, user.getId());
            throw new DataException(DataException.ExceptionCode.NO_FORM_TOKEN_ERROR);
        }
        try {
            String ip = RequestUtil.getIpAdrress(request);
            String orderNo = orderService.submit(productId, num, couponNo, ip, contactType, contactInfo);
            return Result.success().data(orderNo).msg("创建订单成功!");
        } finally {
            redisOpenService.delete(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey));
        }
    }

    /**
     * 订单列表
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数据条数
     * @return 封装结果集
     */
    @RequestMapping(value = "/list")
    public Result orderList(@RequestParam Integer pageNum,
                            @RequestParam Integer pageSize) {
        PageInfo<OrderDetailsVO> page = orderService.list(pageNum, pageSize);
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
        orderService.userCancelOrder(orderNo);
        return Result.success().data(orderNo).msg("取消订单成功!");
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
        playMiniAppPushService.remindReceive(order);
        redisOpenService.setTimeInterval(orderNo, 5 * 60);
        return Result.success().msg("提醒接单成功!");
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
        PayRequestModel model =  PayRequestModel.newBuilder().order(order).user(user).build();
        PayRequestRes res = h5PayService.payRequest(model);
        return Result.success().data(res);
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
        playMiniAppPushService.remindStart(order);
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
     * 用户取消协商
     *
     * @param orderNo
     * @param orderEventId
     * @return
     */
    @RequestMapping(value = "/user/consult-cancel")
    public Result consultCancel(@RequestParam(required = true) String orderNo,
                                @RequestParam(required = true) Integer orderEventId) {
        orderService.consultCancelOrder(orderNo, orderEventId);
        return Result.success().data(orderNo).msg("取消协商成功!");
    }


    /**
     * 用户验收订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/user/verify")
    public Result userVerifyOrder(@RequestParam(required = true) String orderNo) {
        orderService.userVerifyOrder(orderNo);
        return Result.success().data(orderNo).msg("订单验收成功!");
    }


    /**
     * 订单事件查询（查询协商和申诉）
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/event")
    public Result orderEvent(@RequestParam(required = true) String orderNo) {
        User user = userService.getCurrentUser();
        OrderEventVO orderEventVO = orderService.findOrderEvent(orderNo,user.getId());
        return Result.success().data(orderEventVO);
    }


    /**
     * 创建订单留言
     *
     * @param orderNo
     * @param orderEventId
     * @param remark
     * @param fileUrl
     * @return
     */
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
