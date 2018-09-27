package com.fulu.game.app.controller;


import com.fulu.game.app.service.impl.AppOrderPayServiceImpl;
import com.fulu.game.app.service.impl.AppOrderServiceImpl;
import com.fulu.game.app.util.RequestUtil;
import com.fulu.game.common.Result;
import com.fulu.game.common.domain.ClientInfo;
import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.UserTypeEnum;
import com.fulu.game.common.exception.DataException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderDeal;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.payment.model.PayRequestModel;
import com.fulu.game.core.entity.payment.res.PayRequestRes;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.core.service.impl.push.MobileAppPushServiceImpl;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

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
    @Autowired
    private AppOrderPayServiceImpl appPayService;
    @Autowired
    private UserCommentService userCommentService;
    @Autowired
    private ServerCommentService serverCommentService;
    @Autowired
    private MobileAppPushServiceImpl mobileAppPushService;

    @Autowired
    private UserTechAuthService userTechAuthService;

    /**
     * @param productId
     * @param num
     * @param beginTime
     * @param couponNo
     * @param sessionkey
     * @param remark
     * @param request
     * @return
     */
    @RequestMapping(value = "submit")
    public Result submit(@RequestParam(required = true) Integer productId,
                         @RequestParam(required = true) Integer num,
                         @RequestParam(required = true) Date beginTime,
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
            ClientInfo clientInfo = SubjectUtil.getUserClientInfo();
            int platform;
            if (clientInfo != null) {
                if (clientInfo.get_platform().equalsIgnoreCase("ios")) {
                    platform = PlatformEcoEnum.IOS.getType();
                } else {
                    platform = PlatformEcoEnum.ANDROID.getType();
                }
            } else {
                platform = PlatformEcoEnum.ANDROID.getType();
            }

            String ip = RequestUtil.getIpAdrress(request);
            String orderNo = appOrderServiceImpl.submit(productId, num, platform, beginTime, remark, couponNo, ip);
            return Result.success().data(orderNo).msg("创建订单成功!");
        } finally {
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
                      Integer payment) {
        Order order = appOrderServiceImpl.findByOrderNo(orderNo);
        order.setPayment(payment);
        User user = userService.findById(order.getUserId());
        PayRequestModel model = PayRequestModel.newBuilder().order(order).user(user).build();
        PayRequestRes res = appPayService.payRequest(model);
        return Result.success().data(res);
    }


    /**
     * 陪玩师接收订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/receive")
    public Result serverReceiveOrder(@RequestParam String orderNo) {
        appOrderServiceImpl.serverReceiveOrder(orderNo);
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
        Order order =appOrderServiceImpl.findByOrderNo(orderNo);
        appOrderServiceImpl.serverStartServeOrder(order);
        return Result.success().data(orderNo).msg("开始服务!");
    }


    /**
     * 下单页面接口
     * @return
     */
    @RequestMapping(value = "/product")
    public Result orderProduct(@RequestParam(required = true) Integer productId){
        TechProductOrderVO techProductOrderVO = userTechAuthService.getTechProductByProductId(productId);
        return Result.success().data(techProductOrderVO);
    }


    /**
     * 订单状态过滤
     * @return 封装结果集
     */
    @RequestMapping("/filter")
    public Result userStatusList(@RequestParam(required = true) Integer type) {

        List<Map<String, Object>> userFilter = new ArrayList<>();
        List<Map<String, Object>> serverFilter = new ArrayList<>();

        for (OrderStatusGroupEnum groupEnum : OrderStatusGroupEnum.values()) {
            Map<String, Object> statusMap = new LinkedHashMap<>();
            if ("APP_USER".equals(groupEnum.getType())) {
                statusMap.put("status", groupEnum.getValue());
                statusMap.put("name", groupEnum.getName());
                userFilter.add(statusMap);
            }
        }

        for (OrderStatusGroupEnum groupEnum : OrderStatusGroupEnum.values()) {
            Map<String, Object> statusMap = new LinkedHashMap<>();
            if ("APP_SERVER".equals(groupEnum.getType())) {
                statusMap.put("status", groupEnum.getValue());
                statusMap.put("name", groupEnum.getName());
                serverFilter.add(statusMap);
            }
        }

        List<Map<String, Object>> list = null;
        if (UserTypeEnum.GENERAL_USER.getType().equals(type)) {
            list = userFilter;
        } else {
            list = serverFilter;
        }
        return Result.success().data(list);

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
    public Result userOrderList(@RequestParam(required = true) Integer pageNum,
                                @RequestParam(required = true) Integer pageSize,
                                Integer type,
                                Integer filter) {

        List<Integer> statusList = null;
        Integer[] statusArr = OrderStatusGroupEnum.getByValue(filter);
        if (statusArr != null) {
            statusList = Arrays.asList(statusArr);
        }
        PageInfo<OrderDetailsVO> orderList = appOrderServiceImpl.orderList(pageNum, pageSize, type, statusList);
        return Result.success().data(orderList);
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
        appOrderServiceImpl.userConsultOrder(orderNo, refundMoney, remark, fileUrl);
        return Result.success().data(orderNo).msg("提交订单协商!");
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
        Order order = appOrderServiceImpl.findByOrderNo(orderNo);
        User user = userService.getCurrentUser();
        appOrderServiceImpl.consultAgreeOrder(order, orderEventId,user.getId());
        return Result.success().data(orderNo).msg("同意订单协商!");
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
        Order order = appOrderServiceImpl.findByOrderNo(orderNo);
        User user = userService.getCurrentUser();
        appOrderServiceImpl.consultRejectOrder(order, orderEventId, remark, fileUrl,user.getId());
        return Result.success().data(orderNo).msg("拒绝协商订单!");
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
        appOrderServiceImpl.consultCancelOrder(orderNo, orderEventId);
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
        appOrderServiceImpl.userVerifyOrder(orderNo);
        return Result.success().data(orderNo).msg("订单验收成功!");
    }

    /**
     * 用户取消订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/user/cancel")
    public Result userCancelOrder(@RequestParam(required = true) String orderNo, String reason) {
        log.info("{}订单取消,原因:{}", reason);
        appOrderServiceImpl.userCancelOrder(orderNo);
        return Result.success().data(orderNo).msg("取消订单成功!");
    }

    /**
     * 陪玩师取消订单
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/cancel")
    public Result serverCancelOrder(@RequestParam(required = true) String orderNo, String reason) {
        Order order = appOrderServiceImpl.findByOrderNo(orderNo);
        log.info("{}订单取消,原因:{}", reason);
        appOrderServiceImpl.serverCancelOrder(order);
        return Result.success().data(orderNo).msg("取消订单成功!");
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
        OrderEventVO orderEventVO = appOrderServiceImpl.findOrderEvent(orderNo,user);
        return Result.success().data(orderEventVO);
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
        appOrderServiceImpl.userAppealOrder(orderNo, remark, fileUrl);
        return Result.success().data(orderNo).msg("订单申诉成功!");
    }


    /**
     * 留言
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
        OrderDeal orderDeal = appOrderServiceImpl.eventLeaveMessage(orderNo, orderEventId, remark, fileUrl);
        return Result.success().data(orderDeal);
    }


    /**
     * 订单详情
     *
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/details")
    public Result orderDetails(@RequestParam(required = true) String orderNo) {
        OrderDetailsVO orderDetailsVO = appOrderServiceImpl.findOrderDetails(orderNo);
        return Result.success().data(orderDetailsVO);
    }


    /**
     * 用户-添加评价
     *
     * @return
     */
    @RequestMapping("/comment/save")
    public Result save(UserCommentVO commentVO) {
        userCommentService.save(commentVO);
        return Result.success().msg("评论成功！");
    }


    /**
     * 查看订单评论
     *
     * @return
     */
    @RequestMapping("/comment/get")
    public Result get(@RequestParam("orderNo") String orderNo) {
        UserCommentVO comment = userCommentService.findByOrderNo(orderNo);
        if (null == comment) {
            return Result.error().msg("该评论不存在！");
        }
        return Result.success().data(comment).msg("查询成功！");
    }


    /**
     * 陪玩师-添加评价
     *
     * @return
     */
    @RequestMapping("/server-comment/save")
    public Result serverCommentSave(ServerCommentVO serverCommentVO) {
        serverCommentService.save(serverCommentVO);
        return Result.success().msg("评论成功！");
    }

    /**
     * 陪玩师-查询评价
     *
     * @return
     */
    @RequestMapping("/server-comment/get")
    public Result serverCommentGet(@RequestParam("orderNo") String orderNo) {
        //此处评论信息不存在的话，也需要返回用户昵称、头像、性别、年龄和im信息
        ServerCommentVO serverCommentVO = serverCommentService.findCommentInfoByOrderNo(orderNo);
        return Result.success().data(serverCommentVO).msg("获取评论成功！");
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
        Order order = appOrderServiceImpl.findByOrderNo(orderNo);
        mobileAppPushService.remindReceive(order);
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
        Order order = appOrderServiceImpl.findByOrderNo(orderNo);
        mobileAppPushService.remindStart(order);
        redisOpenService.setTimeInterval(orderNo, 5 * 60);
        return Result.success().msg("提醒开始成功!");
    }
}
