package com.fulu.game.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.properties.Config;
import com.fulu.game.common.threadpool.SpringThreadPoolExecutor;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.common.utils.MailUtil;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.OrderEventVO;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.aop.UserScore;
import com.fulu.game.core.service.impl.push.MiniAppPushServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.util.*;

import static com.fulu.game.common.enums.OrderStatusEnum.NON_PAYMENT;

@Slf4j
@Service
public abstract class AbOrderOpenServiceImpl implements OrderOpenService {

    @Autowired
    private OrderMoneyDetailsService orderMoneyDetailsService;
    @Autowired
    private OrderDealService orderDealService;
    @Autowired
    private UserService userService;
    @Autowired
    private PlatformMoneyDetailsService platformMoneyDetailsService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private OrderStatusDetailsService orderStatusDetailsService;
    @Autowired
    private OrderEventService orderEventService;
    @Autowired
    private UserAutoReceiveOrderService userAutoReceiveOrderService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private SpringThreadPoolExecutor springThreadPoolExecutor;
    @Autowired
    private ImService imService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private Config configProperties;


    /**
     * 确认订单支付
     *
     * @param order
     */
    protected abstract void dealOrderAfterPay(Order order);

    /**
     * 分润
     *
     * @param order
     */
    protected abstract void shareProfit(Order order);

    /**
     * 退款
     *
     * @param order
     * @param refundMoney
     */
    protected abstract void orderRefund(Order order, BigDecimal refundMoney);


    /**
     * 子类重写
     *
     * @return
     */
    protected abstract MiniAppPushServiceImpl getMinAppPushService();


    /**
     * 上分订单抢单  PS:为什么这个方法放在core，是因为shedule和point都在调用
     *
     * @param orderNo
     * @return
     */
    public String receivePointOrder(String orderNo, User serviceUser) {
        Order order = orderService.findByOrderNo(orderNo);
        if (order.getUserId().equals(serviceUser.getId())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_NOT_ROB_MYSELF, orderNo);
        }
        log.info("陪玩师抢单:userId:{};order:{}", serviceUser.getId(), order);
        if (!OrderTypeEnum.POINT.getType().equals(order.getType())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_TYPE_MISMATCHING, order.getOrderNo());
        }
        if (order.getServiceUserId() != null || !OrderStatusEnum.WAIT_SERVICE.getStatus().equals(order.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_ALREADY_RECEIVE, order.getOrderNo());
        }
        try {
            if (!redisOpenService.lock(RedisKeyEnum.MARKET_ORDER_RECEIVE_LOCK.generateKey(order.getOrderNo()), 30)) {
                throw new OrderException(OrderException.ExceptionCode.ORDER_ALREADY_RECEIVE, order.getOrderNo());
            }
            order.setServiceUserId(serviceUser.getId());
            order.setReceivingTime(new Date());
            order.setStatus(OrderStatusEnum.ALREADY_RECEIVING.getStatus());
            order.setUpdateTime(new Date());
            orderService.update(order);
            log.info("抢单成功:userId:{};order:{}", serviceUser.getId(), order);
            //倒计时订单状态
            orderStatusDetailsService.create(orderNo, order.getStatus(), 10);
            //增加接单数量
            userAutoReceiveOrderService.addOrderNum(serviceUser.getId(), order.getCategoryId());

            //推送通知
            getMinAppPushService().serviceUserAcceptOrder(order);

            //开启新线程通知老板，陪玩师已接单
            springThreadPoolExecutor.getAsyncExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    //方案一：长轮询通知老板
                    order.setServiceUserId(serviceUser.getId());
                    Constant.serviceUserAcceptOrderMap.put(order.getUserId(), order);
                    //方案二：发送IM消息通知老板
                    User bossUser = userService.findById(order.getUserId());
                    String imId = bossUser.getImId();
                    if (StringUtils.isNotBlank(imId)) {
                        imService.sendMsgToImUser(imId, Constant.SERVICE_USER_ACCEPT_ORDER);
                    }
                }
            });
        } finally {
            redisOpenService.unlock(RedisKeyEnum.MARKET_ORDER_RECEIVE_LOCK.generateKey(order.getOrderNo()));
        }
        return orderNo;
    }

    @Override
    public DeferredResult<Result> getServiceUserAcceptOrderStatus() {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }

        Integer userId = user.getId();
        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("flag", Constant.SERVICE_USER_NOT_ACCEPT_ORDER);
        Result defaultResult = Result.success().data(resultMap).msg("60秒内没有陪玩师接单！");

        //超时时间:60秒
        DeferredResult<Result> deferredResult = new DeferredResult<>(Constant.MILLI_SECOND_60, defaultResult);

        Order order = new Order();

        long startTimeMillis = System.currentTimeMillis();
        while (true) {
            Long threadSleepTime = 5000L;
            if (Constant.serviceUserAcceptOrderMap.containsKey(userId)) {
                order = (Order) Constant.serviceUserAcceptOrderMap.get(userId);
                log.info("陪玩师id:{}已接单，订单编号:{}！", order.getServiceUserId(), order.getOrderNo());
                resultMap.put("flag", Constant.SERVICE_USER_ACCEPT_ORDER);
                Result result = Result.success().data(resultMap).msg("陪玩师已接单！");
                deferredResult.setResult(result);
                Constant.serviceUserAcceptOrderMap.remove(userId);
                return deferredResult;
            }

            long currentTimeMillis = System.currentTimeMillis();
            long resultTimeMillis = currentTimeMillis - startTimeMillis;
            if (resultTimeMillis >= Constant.MILLI_SECOND_60 - 5000L) {
                threadSleepTime = 1000L;
            }
            if (resultTimeMillis >= Constant.MILLI_SECOND_60 - 1000L) {
                deferredResult.setResult(defaultResult);
                break;
            }
            try {
                Thread.sleep(threadSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return deferredResult;
    }


    @Override
    public OrderDeal eventLeaveMessage(String orderNo, Integer eventId, String remark, String... fileUrl) {
        Order order = orderService.findByOrderNo(orderNo);
        OrderEvent orderEvent = orderEventService.findById(eventId);
        if (!orderEvent.getOrderNo().equals(order.getOrderNo())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, orderNo);
        }
        User user = userService.getCurrentUser();
        OrderDeal orderDeal = new OrderDeal();
        orderDeal.setTitle("上传凭证");
        orderDeal.setType(OrderDealTypeEnum.CONSULT.getType());
        orderDeal.setUserId(user.getId());
        orderDeal.setRemark(remark);
        orderDeal.setOrderNo(order.getOrderNo());
        orderDeal.setOrderEventId(orderEvent.getId());
        orderDeal.setCreateTime(new Date());
        orderDealService.create(orderDeal, fileUrl);
        return orderDeal;
    }

    /**
     * 使用优惠券
     *
     * @return
     */
    protected Coupon useCouponForOrder(String couponCode, Order order) {
        Coupon coupon = couponService.findByCouponNo(couponCode);
        //判断是否是自己的优惠券
        userService.isCurrentUser(coupon.getUserId());
        //判断该优惠券是否可用
        if (!couponService.couponIsAvailable(coupon)) {
            return null;
        }
        order.setCouponNo(coupon.getCouponNo());
        order.setCouponMoney(coupon.getDeduction());
        //判断优惠券金额是否大于订单总额
        if (coupon.getDeduction().compareTo(order.getTotalMoney()) >= 0) {
            order.setActualMoney(new BigDecimal(0));
            order.setCouponMoney(order.getTotalMoney());
        } else {
            BigDecimal actualMoney = order.getTotalMoney().subtract(coupon.getDeduction());
            order.setActualMoney(actualMoney);
        }
        return coupon;
    }

    /**
     * 订单支付
     *
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO payOrder(String orderNo, BigDecimal orderMoney) {
        log.info("用户支付订单orderNo:{},orderMoney:{}", orderNo, orderMoney);
        Order order = orderService.findByOrderNo(orderNo);
        if (order.getIsPay()) {
            throw new OrderException(orderNo, "重复支付订单![" + order.toString() + "]");
        }
        order.setIsPay(true);
        order.setIsPayCallback(true);
        order.setStatus(OrderStatusEnum.WAIT_SERVICE.getStatus());
        order.setUpdateTime(new Date());
        order.setPayTime(new Date());
        orderService.update(order);
        //记录平台流水
        platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.ORDER_PAY, order.getOrderNo(), order.getTotalMoney());
        if (order.getCouponNo() != null) {
            platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.COUPON_DEDUCTION, order.getOrderNo(), order.getCouponMoney().negate());
        }
        //记录订单流水
        orderMoneyDetailsService.create(order.getOrderNo(), order.getUserId(), DetailsEnum.ORDER_PAY, orderMoney);

        dealOrderAfterPay(order);

        return orderConvertVo(order);
    }


    /**
     * 陪玩师开始服务
     *
     * @return
     */
    @Override
    public String serverStartServeOrder(Order order) {
        log.info("陪玩师接单orderNo:{}", order.getOrderNo());
        
        if (!order.getStatus().equals(OrderStatusEnum.ALREADY_RECEIVING.getStatus()) &&
                !order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, order.getOrderNo());
        }
        order.setStatus(OrderStatusEnum.SERVICING.getStatus());
        order.setUpdateTime(new Date());
        orderService.update(order);
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        //推送通知
        getMinAppPushService().start(order);
        return order.getOrderNo();
    }

    /**
     * 申请协商处理
     *
     * @param orderNo
     * @param refundMoney
     * @param remark
     * @param fileUrls
     * @return
     */
    @Override
    public String userConsultOrder(String orderNo, BigDecimal refundMoney, String remark, String[] fileUrls) {
        log.info("用户协商订单orderNo:{}", orderNo);
        Order order = orderService.findByOrderNo(orderNo);
        if (refundMoney == null) {
            throw new OrderException(orderNo, "协商处理金额不能为空!");
        }
        String refundType = "";
        if (refundMoney.compareTo(order.getActualMoney()) > 0) {
            throw new OrderException(orderNo, "协商处理金额不能大于订单支付金额!");
        }
        if (refundMoney.compareTo(order.getActualMoney()) == 0) {
            refundType = "全部退款";
        } else {
            refundType = "部分退款";
        }
        User user = userService.getCurrentUser();
        userService.isCurrentUser(order.getUserId());
        if (!order.getStatus().equals(OrderStatusEnum.ALREADY_RECEIVING.getStatus()) &&
                !order.getStatus().equals(OrderStatusEnum.SERVICING.getStatus()) &&
                !order.getStatus().equals(OrderStatusEnum.CHECK.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, orderNo);
        }
        //提交申诉
        OrderEvent orderEvent = orderEventService.createConsult(order, user, order.getStatus(), refundMoney);
        order.setStatus(OrderStatusEnum.CONSULTING.getStatus());
        order.setUpdateTime(new Date());
        orderService.update(order);
        //提交协商处理
        String title = "发起了协商-" + refundType + " ￥" + refundMoney.toPlainString();
        OrderDeal orderDeal = new OrderDeal();
        orderDeal.setTitle(title);
        orderDeal.setType(OrderDealTypeEnum.CONSULT.getType());
        orderDeal.setUserId(user.getId());
        orderDeal.setRemark(remark);
        orderDeal.setOrderNo(order.getOrderNo());
        orderDeal.setOrderEventId(orderEvent.getId());
        orderDeal.setCreateTime(new Date());
        orderDealService.create(orderDeal, fileUrls);
        //倒计时24小时后处理
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 24 * 60);
        //推送通知
        getMinAppPushService().consult(order);
        return orderNo;
    }

    @Override
    public OrderEventVO findOrderEvent(String orderNo) {
        Order order = orderService.findByOrderNo(orderNo);
        if (order == null) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_NOT_EXIST, orderNo);
        }
        int type = OrderEventTypeEnum.CONSULT.getType();
        if (Arrays.asList(OrderStatusGroupEnum.CONSULT_ALL.getStatusList()).contains(order.getStatus())) {
            type = OrderEventTypeEnum.CONSULT.getType();
        } else if (Arrays.asList(OrderStatusGroupEnum.APPEAL_ALL.getStatusList()).contains(order.getStatus())) {
            type = OrderEventTypeEnum.APPEAL.getType();
        }
        User user = userService.getCurrentUser();
        OrderEventVO orderEventVO = orderEventService.getOrderEvent(order, user, type);
        if (orderEventVO == null) {
            throw new OrderException(orderNo, "该协商已经被取消!");
        }
        User currentUser = userService.getCurrentUser();
        if (currentUser.getId().equals(orderEventVO.getUserId())) {
            orderEventVO.setIdentity(UserTypeEnum.GENERAL_USER.getType());
        } else if (currentUser.getId().equals(orderEventVO.getServiceUserId())) {
            orderEventVO.setIdentity(UserTypeEnum.ACCOMPANY_PLAYER.getType());
        } else {
            throw new ServiceErrorException("用户不匹配!");
        }
        return orderEventVO;
    }

    /**
     * 拒绝协商处理
     *
     * @param orderConsultId
     * @param remark
     * @param fileUrls
     * @return
     */
    @Override
    public String consultRejectOrder(Order order,
                                     int orderConsultId,
                                     String remark,
                                     String[] fileUrls , Integer userId) {
        log.info("拒绝协商处理订单orderNo:{}", order.getOrderNo());
        
        OrderEvent orderEvent = orderEventService.findById(orderConsultId);
        if (orderEvent == null || !order.getOrderNo().equals(orderEvent.getOrderNo())) {
            throw new OrderException(order.getOrderNo(), "拒绝协商订单不匹配!");
        }
        
        if (!order.getStatus().equals(OrderStatusEnum.CONSULTING.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, order.getOrderNo());
        }
        order.setStatus(OrderStatusEnum.CONSULT_REJECT.getStatus());
        order.setUpdateTime(new Date());
        orderService.update(order);
        OrderDeal orderDeal = new OrderDeal();
        orderDeal.setTitle("拒绝了协商");
        orderDeal.setType(OrderDealTypeEnum.CONSULT.getType());
        orderDeal.setUserId(userId);
        orderDeal.setRemark(remark);
        orderDeal.setOrderNo(order.getOrderNo());
        orderDeal.setOrderEventId(orderEvent.getId());
        orderDeal.setCreateTime(new Date());
        orderDealService.create(orderDeal, fileUrls);
        //倒计时24小时后处理
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 24 * 60);
        //推送通知
        getMinAppPushService().rejectConsult(order);
        return order.getOrderNo();
    }

    /**
     * 协商解决完成
     *
     * @param orderEventId
     * @return
     */
    @Override
    @UserScore(type = UserScoreEnum.CONSULT)
    public String consultAgreeOrder(Order order, int orderEventId , Integer userId) {
        log.info("陪玩师同意协商处理订单orderNo:{}", order.getOrderNo());
        
        OrderEvent orderEvent = orderEventService.findById(orderEventId);
        if (orderEvent == null || !order.getOrderNo().equals(orderEvent.getOrderNo())) {
            throw new OrderException(order.getOrderNo(), "拒绝协商订单不匹配!");
        }
        User user = userService.getCurrentUser();
        userService.isCurrentUser(order.getServiceUserId());
        if (!order.getStatus().equals(OrderStatusEnum.CONSULTING.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, order.getOrderNo());
        }
        order.setStatus(OrderStatusEnum.CONSULT_COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        orderService.update(order);
        String title = "同意了协商，￥" + orderEvent.getRefundMoney().toPlainString() + "已经退款结算";
        OrderDeal orderDeal = new OrderDeal();
        orderDeal.setTitle(title);
        orderDeal.setType(OrderDealTypeEnum.CONSULT.getType());
        orderDeal.setUserId(userId);
        orderDeal.setRemark("陪玩师同意协商");
        orderDeal.setOrderNo(order.getOrderNo());
        orderDeal.setOrderEventId(orderEvent.getId());
        orderDeal.setCreateTime(new Date());
        orderDealService.create(orderDeal, null);
        //退款给用户
        orderRefund(order, orderEvent.getRefundMoney());
        //创建订单状态详情
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        //推送通知同意协商
        getMinAppPushService().agreeConsult(order);
        return order.getOrderNo();
    }


    /**
     * 用户取消协商
     *
     * @param orderNo
     * @param orderEventId
     * @return
     */
    @Override
    public String consultCancelOrder(String orderNo, int orderEventId) {
        log.info("取消协商处理订单orderNo:{}", orderNo);
        Order order = orderService.findByOrderNo(orderNo);
        OrderEvent orderEvent = orderEventService.findById(orderEventId);
        if (orderEvent == null || !order.getOrderNo().equals(orderEvent.getOrderNo())) {
            throw new OrderException(orderNo, "拒绝协商订单不匹配!");
        }
        User user = userService.getCurrentUser();
        userService.isCurrentUser(order.getUserId());
        if (!order.getStatus().equals(OrderStatusEnum.CONSULTING.getStatus()) && !order.getStatus().equals(OrderStatusEnum.CONSULT_REJECT.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, orderNo);
        }
        orderEventService.cancelConsult(order, user, orderEvent);
        log.info("取消协商处理更改订单状态前:{}", order);
        //订单状态重置,时间重置
        order.setStatus(orderEvent.getOrderStatus());
        order.setUpdateTime(new Date());
        orderService.update(order);
        log.info("取消协商处理更改订单状态后:{}", order);
        //推送通知
        getMinAppPushService().cancelConsult(order);
        return order.getOrderNo();
    }

    /**
     * 陪玩师取消订单
     *
     * @param orderNo
     * @return
     */
    @Override
    @UserScore(type = UserScoreEnum.SERVICE_USER_CANCEL_ORDER)
    public OrderVO serverCancelOrder(Order order) {
        log.info("陪玩师取消订单orderNo:{}", order.getOrderNo());
        
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, order.getOrderNo());
        }
        order.setStatus(OrderStatusEnum.SERVER_CANCEL.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        orderService.update(order);
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        //消息提醒
        getMinAppPushService().cancelOrderByServer(order);
        // 全额退款用户
        if (order.getIsPay()) {
            //TODO 退款
            orderRefund(order, order.getActualMoney());
        }
        
        //发送邮件
        MailUtil.sendMail(configProperties.getOrdermail().getAddress(),configProperties.getOrdermail().getPassword(),"陪玩师取消了订单："+order.getOrderNo(),"陪玩师取消了订单，订单号"+order.getOrderNo(),new String[]{configProperties.getOrdermail().getAddress()});
        return orderConvertVo(order);
    }
    
    

    /**
     * 用户取消订单
     *
     * @param orderNo
     * @return
     */
    @Override
    @UserScore(type = UserScoreEnum.USER_CANCEL_ORDER)
    public OrderVO userCancelOrder(String orderNo) {
        log.info("用户取消订单orderNo:{}", orderNo);
        Order order = orderService.findByOrderNo(orderNo);
        userService.isCurrentUser(order.getUserId());
        if (!order.getStatus().equals(NON_PAYMENT.getStatus())
                && !order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有等待陪玩和未支付的订单才能取消!");
        }
        if (order.getIsPay()) {
            order.setStatus(OrderStatusEnum.USER_CANCEL.getStatus());
        } else {
            order.setStatus(OrderStatusEnum.UNPAY_ORDER_CLOSE.getStatus());
        }

        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        orderService.update(order);
        // 全额退款用户
        if (order.getIsPay()) {
            orderRefund(order, order.getActualMoney());
            getMinAppPushService().cancelOrderByUser(order);
        }
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        return orderConvertVo(order);
    }


    /**
     * 用户仲裁订单
     *
     * @param orderNo
     * @param remark
     * @param fileUrl
     * @return
     */
    @Override
    public String userAppealOrder(String orderNo,
                                  String remark,
                                  String... fileUrl) {
        log.info("用户申诉订单orderNo:{}", orderNo);
        Order order = orderService.findByOrderNo(orderNo);
        User user = userService.getCurrentUser();
        if (!order.getStatus().equals(OrderStatusEnum.CONSULTING.getStatus())
                && !order.getStatus().equals(OrderStatusEnum.CONSULT_REJECT.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有协商中协商拒绝的订单才能申诉仲裁!");
        }
        order.setStatus(OrderStatusEnum.APPEALING.getStatus());
        order.setUpdateTime(new Date());
        orderService.update(order);
        //创建仲裁事件
        orderEventService.createAppeal(order, user, remark, fileUrl);
        //两小时倒计时
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 2 * 60);
        //TODO 推送做抽象
        //推送通知
        if (user.getId().equals(order.getUserId())) {
            getMinAppPushService().appealByUser(order);
        } else {
            getMinAppPushService().appealByServer(order);
        }

        userAutoReceiveOrderService.addOrderDisputeNum(order.getServiceUserId(), order.getCategoryId());

        return orderNo;
    }


    /**
     * 陪玩师提交验收订单
     *
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO serverAcceptanceOrder(String orderNo, String remark, String[] fileUrl) {
        log.info("打手提交验收订单orderNo:{}", orderNo);
        Order order = orderService.findByOrderNo(orderNo);
        userService.isCurrentUser(order.getServiceUserId());
        User user = userService.getCurrentUser();
        if (!order.getStatus().equals(OrderStatusEnum.SERVICING.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有陪玩中的订单才能验收!");
        }
        order.setStatus(OrderStatusEnum.CHECK.getStatus());
        order.setUpdateTime(new Date());
        orderService.update(order);
        //24小时自动验收
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 24 * 60);
        //提交验收订单
        orderEventService.createCheckEvent(order, user, remark, fileUrl);
        //推送通知
        getMinAppPushService().checkOrder(order);
        return orderConvertVo(order);
    }

    /**
     * 用户验收订单
     *
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO userVerifyOrder(String orderNo) {
        log.info("用户验收订单orderNo:{}", orderNo);
        Order order = orderService.findByOrderNo(orderNo);
        userService.isCurrentUser(order.getUserId());
        if (!order.getStatus().equals(OrderStatusEnum.CHECK.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有待验收订单才能验收!");
        }
        order.setStatus(OrderStatusEnum.COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        orderService.update(order);
        //订单分润
        shareProfit(order);
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        //确认服务
        getMinAppPushService().acceptOrder(order);
        return orderConvertVo(order);
    }


    @Override
    public Order findByOrderNo(String orderNo) {
        return orderService.findByOrderNo(orderNo);
    }


    protected OrderVO orderConvertVo(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtil.copyProperties(order, orderVO);
        return orderVO;
    }

    /**
     * 生成订单号
     *
     * @return
     */
    protected String generateOrderNo() {
        String orderNo = GenIdUtil.GetOrderNo();
        if (orderService.findByOrderNo(orderNo) == null) {
            return orderNo;
        } else {
            return generateOrderNo();
        }
    }
}
