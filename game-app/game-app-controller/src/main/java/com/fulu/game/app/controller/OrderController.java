package com.fulu.game.app.controller;


import com.fulu.game.app.service.impl.AppOrderServiceImpl;
import com.fulu.game.app.service.impl.AppOrderPayServiceImpl;
import com.fulu.game.app.util.RequestUtil;
import com.fulu.game.common.Result;
import com.fulu.game.common.domain.ClientInfo;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.DataException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderDeal;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserComment;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.service.OrderEventService;
import com.fulu.game.core.service.UserCommentService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

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

    @RequestMapping(value = "submit")
    public Result submit(@RequestParam(required = true) Integer productId,
                         @RequestParam(required = true) Integer num,
                         @RequestParam(required = true) Integer payment,
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
            if (clientInfo.get_platform().equalsIgnoreCase("ios")) {
                platform = PlatformEcoEnum.IOS.getType();
            } else {
                platform = PlatformEcoEnum.ANDROID.getType();
            }
            String ip = RequestUtil.getIpAdrress(request);
            String orderNo = appOrderServiceImpl.submit(productId, num, payment, platform, beginTime, remark, couponNo, ip);
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
                      Integer payment,
                      HttpServletRequest request) {
        String ip = RequestUtil.getIpAdrress(request);
        Order order = appOrderServiceImpl.findByOrderNo(orderNo);
        order.setPayment(payment);
        User user = userService.findById(order.getUserId());
        Object result = appPayService.payOrder(order, user, ip);
        if(result==null){
            PayRequestVO payRequestVO = new PayRequestVO();
            payRequestVO.setPayment(0);
            payRequestVO.setPayArguments(true);
            result = payRequestVO;
        }

        return Result.success().data(result);
    }



    /**
     * 陪玩师接收订单
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
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/server/start-serve")
    public Result startServerOrder(@RequestParam(required = true) String orderNo) {
        appOrderServiceImpl.serverStartServeOrder(orderNo);
        return Result.success().data(orderNo).msg("开始服务!");
    }



    /**
     * 订单列表
     * @param pageNum
     * @param pageSize
     * @param type
     * @return
     */
    @RequestMapping(value = "/list")
    public Result userOrderList(@RequestParam(required = true) Integer pageNum,
                                @RequestParam(required = true) Integer pageSize,
                                Integer type){
        PageInfo<OrderDetailsVO>  orderList = appOrderServiceImpl.orderList(pageNum,pageSize,type);
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
        appOrderServiceImpl.consultAgreeOrder(orderNo, orderEventId);
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
        appOrderServiceImpl.consultRejectOrder(orderNo, orderEventId, remark, fileUrl);
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
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "/user/cancel")
    public Result userCancelOrder(@RequestParam(required = true) String orderNo,String reason) {
        log.info("{}订单取消,原因:{}",reason);
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
    public Result serverCancelOrder(@RequestParam(required = true) String orderNo,String reason) {
        log.info("{}订单取消,原因:{}",reason);
        appOrderServiceImpl.serverCancelOrder(orderNo);
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
        OrderEventVO orderEventVO = appOrderServiceImpl.findOrderEvent(orderNo);
        return Result.success().data(orderEventVO);
    }



    /**
     * 申请客服仲裁
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

}
