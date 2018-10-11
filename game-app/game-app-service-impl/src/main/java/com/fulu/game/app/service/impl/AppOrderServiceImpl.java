package com.fulu.game.app.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.common.enums.UserTypeEnum;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.ProductException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.OrderDetailsVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.AbOrderOpenServiceImpl;
import com.fulu.game.core.service.impl.push.AppPushServiceImpl;
import com.fulu.game.core.service.impl.push.PushServiceImpl;
import com.fulu.game.core.service.impl.push.SMSPushServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AppOrderServiceImpl extends AbOrderOpenServiceImpl {

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private OrderProductService orderProductService;
    @Autowired
    private OrderStatusDetailsService orderStatusDetailsService;
    @Autowired
    private AppPushServiceImpl appPushServiceImpl;
    @Autowired
    private AppOrderShareProfitServiceImpl appOrderShareProfitService;
    @Autowired
    private UserCommentService userCommentService;
    @Autowired
    private ServerCommentService serverCommentService;
    @Autowired
    private UserCommentTagService userCommentTagService;
    @Autowired
    private SMSPushServiceImpl smsPushService;

    /**
     * 用户提交订单
     *
     * @param productId
     * @param num
     * @param platform
     * @param beginTime
     * @param remark
     * @param couponNo
     * @param userIp
     * @return
     */
    public String submit(int productId,
                         int num,
                         int platform,
                         Date beginTime,
                         String remark,
                         String couponNo,
                         String userIp) {
        log.info("用户提交订单productId:{},num:{},remark:{}", productId, num, remark);
        User user = userService.getCurrentUser();
        Product product = productService.findById(productId);
        if (product == null) {
            throw new ProductException(ProductException.ExceptionCode.PRODUCT_NOT_EXIST);
        }
        Category category = categoryService.findById(product.getCategoryId());
        //计算订单总价格
        BigDecimal totalMoney = product.getPrice().multiply(new BigDecimal(num));
        //创建订单
        Order order = new Order();
        order.setName(product.getProductName() + " " + num + "*" + product.getUnit());
        order.setType(OrderTypeEnum.PLAY.getType());
        order.setOrderNo(generateOrderNo());
        order.setUserId(user.getId());
        order.setServiceUserId(product.getUserId());
        order.setCategoryId(product.getCategoryId());
        order.setRemark(remark);
        order.setBeginTime(beginTime);
        order.setPlatform(platform);
        order.setIsPay(false);
        order.setIsPayCallback(false);
        order.setTotalMoney(totalMoney);
        order.setActualMoney(totalMoney);
        order.setStatus(OrderStatusEnum.NON_PAYMENT.getStatus());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setOrderIp(userIp);
        order.setCharges(category.getCharges());
        //使用优惠券
        Coupon coupon = null;
        if (StringUtils.isNotBlank(couponNo)) {
            coupon = useCouponForOrder(couponNo, order);
            if (coupon == null) {
                throw new OrderException(OrderException.ExceptionCode.ORDER_COUPON_NOT_USE);
            }
        }
        if (order.getUserId().equals(order.getServiceUserId())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_NOT_MYSELF);
        }
        //创建订单
        orderService.create(order);
        //更新优惠券使用状态
        if (coupon != null) {
            couponService.updateCouponUseStatus(order.getOrderNo(), userIp, coupon);
        }
        //创建订单商品
        orderProductService.create(order, product, num);

        int countdownMinute = waitForPayTime(order.getCreateTime(), beginTime);
        //计算订单状态倒计时24小时
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), countdownMinute);

        return order.getOrderNo();
    }


    /**
     * 陪玩师接收订单
     *
     * @param orderNo
     * @return
     */
    public String serverReceiveOrder(String orderNo) {
        log.info("陪玩师接单orderNo:{}", orderNo);
        Order order = orderService.findByOrderNo(orderNo);
        userService.isCurrentUser(order.getServiceUserId());
        //只有等待陪玩和已支付的订单才能开始陪玩
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus()) || !order.getIsPay()) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, orderNo);
        }
        order.setStatus(OrderStatusEnum.ALREADY_RECEIVING.getStatus());
        order.setUpdateTime(new Date());
        order.setReceivingTime(new Date());
        orderService.update(order);
        //倒计时时间
        int minute = beginOrderTime(order.getReceivingTime(), order.getBeginTime());
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), minute);
        //todo 陪玩师接单通知
        appPushServiceImpl.receiveOrder(order);
        return order.getOrderNo();
    }


    public OrderDetailsVO findOrderDetails(String orderNo) {
        OrderDetailsVO orderDetailsVO = new OrderDetailsVO();

        User currentUser = userService.getCurrentUser();
        Order order = orderService.findByOrderNo(orderNo);
        if (currentUser.getId().equals(order.getUserId())) {
            orderDetailsVO.setIdentity(UserTypeEnum.GENERAL_USER.getType());
        } else if (order.getServiceUserId().equals(currentUser.getId())) {
            orderDetailsVO.setIdentity(UserTypeEnum.ACCOMPANY_PLAYER.getType());
        } else {
            throw new ServiceErrorException("用户不匹配!");
        }
        OrderProduct orderProduct = orderProductService.findByOrderNo(orderNo);
        BeanUtil.copyProperties(order, orderDetailsVO);

        List<Integer> invisibleContactList = Arrays.asList(OrderStatusGroupEnum.ORDER_CONTACT_INVISIBLE.getStatusList());
        if (invisibleContactList.contains(order.getStatus())) {
            orderDetailsVO.setContactInfo(null);
        }
        Category category = categoryService.findById(order.getCategoryId());

        orderDetailsVO.setCategoryIcon(category.getIcon());
        orderDetailsVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderDetailsVO.getStatus()));
        orderDetailsVO.setStatusNote(OrderStatusEnum.getNoteByStatus(orderDetailsVO.getStatus()));
        orderDetailsVO.setCategoryName(category.getName());

        User server = userService.findById(order.getServiceUserId());
        orderDetailsVO.setServerHeadUrl(server.getHeadPortraitsUrl());
        orderDetailsVO.setServerNickName(server.getNickname());
        orderDetailsVO.setServerAge(server.getAge());
        orderDetailsVO.setServerGender(server.getGender());
        orderDetailsVO.setServerScoreAvg(server.getScoreAvg());
        if (orderDetailsVO.getCharges() == null) {
            orderDetailsVO.setCharges(new BigDecimal("0.1"));
        }
        orderDetailsVO.setServerIncome(orderDetailsVO.getTotalMoney().multiply(orderDetailsVO.getCharges()));

        User user = userService.findById(order.getUserId());
        orderDetailsVO.setUserHeadUrl(user.getHeadPortraitsUrl());
        orderDetailsVO.setUserNickName(user.getNickname());
        orderDetailsVO.setServerAge(user.getAge());
        orderDetailsVO.setServerGender(user.getGender());
        orderDetailsVO.setUserScoreAvg(user.getServerScoreAvg());

        //orderStatus
        long countDown = orderStatusDetailsService.getCountDown(orderNo, order.getStatus());
        orderDetailsVO.setCountDown(countDown);
        orderDetailsVO.setProductId(orderProduct.getProductId());

        //用户评论
        UserComment userComment = userCommentService.findByOrderNo(orderNo);
        if (userComment != null) {
            orderDetailsVO.setCommentContent(userComment.getContent());
            orderDetailsVO.setCommentScore(userComment.getScore());
            List<UserCommentTag> userCommentTags = userCommentTagService.findByCommentId(userComment.getId());
            orderDetailsVO.setCommentTags(userCommentTags);
        }
        return orderDetailsVO;
    }


    @Override
    protected void dealOrderAfterPay(Order order) {
        int minute = receiveOrderTime(order.getPayTime(), order.getBeginTime());
        //订单状态倒计时
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), minute);
        //发送短信通知给陪玩师
        User server = userService.findById(order.getServiceUserId());
        smsPushService.sendOrderReceivingRemind(server.getMobile(), order.getName());
        //推送app通知
        appPushServiceImpl.orderPay(order);
    }


    /**
     * 用户验收订单
     *
     * @param orderNo
     * @return
     */
    @Override
    public String userVerifyOrder(String orderNo) {
        log.info("用户验收订单orderNo:{}", orderNo);
        Order order = orderService.findByOrderNo(orderNo);
        userService.isCurrentUser(order.getUserId());
        if (!order.getStatus().equals(OrderStatusEnum.SERVICING.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有进行中的订单才能验收!");
        }
        order.setStatus(OrderStatusEnum.COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        orderService.update(order);
        //订单分润
        shareProfit(order);
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        //确认服务
        getPushService().acceptOrder(order);
        return orderNo;
    }


    /**
     * 订单列表
     *
     * @param pageNum
     * @param pageSize
     * @param type     1是用户2是陪玩师
     * @return
     */
    public PageInfo<OrderDetailsVO> orderList(int pageNum, int pageSize, Integer type, List<Integer> statusList) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        User user = userService.getCurrentUser();
        List<OrderDetailsVO> list = orderService.orderList(type, user.getId(), statusList);
        for (OrderDetailsVO orderDetailsVO : list) {
            String categoryName = orderDetailsVO.getName().substring(0, orderDetailsVO.getName().indexOf(" "));
            orderDetailsVO.setCategoryName(categoryName);
            orderDetailsVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderDetailsVO.getStatus()));
            orderDetailsVO.setStatusNote(OrderStatusEnum.getNoteByStatus(orderDetailsVO.getStatus()));
            OrderProduct orderProduct = orderProductService.findByOrderNo(orderDetailsVO.getOrderNo());
            orderDetailsVO.setPriceUnit(orderProduct.getUnit() + "/" + orderProduct.getPrice() + "元*" + orderProduct.getAmount());
            //订单已评价状态显示订单评价的分数
            if (orderDetailsVO.getStatus().equals(OrderStatusEnum.ALREADY_APPRAISE.getStatus())) {
                UserComment userComment = userCommentService.findByOrderNo(orderDetailsVO.getOrderNo());
                if (userComment != null) {
                    orderDetailsVO.setCommentScore(userComment.getScore());
                }
            }

            if (orderDetailsVO.getStatus().equals(OrderStatusEnum.COMPLETE.getStatus())
                    || orderDetailsVO.getStatus().equals(OrderStatusEnum.SYSTEM_COMPLETE.getStatus())
                    || orderDetailsVO.getStatus().equals(OrderStatusEnum.ALREADY_APPRAISE.getStatus())) {
                ServerComment serverComment = serverCommentService.findByOrderNo(orderDetailsVO.getOrderNo());
                boolean flag = (serverComment != null);
                orderDetailsVO.setIsCommentedUser(flag);
            }


        }
        return new PageInfo<>(list);
    }


    @Override
    protected void shareProfit(Order order) {
        appOrderShareProfitService.shareProfit(order);
        //todo 添加陪玩师相关技能的单数

    }


    @Override
    protected void orderRefund(Order order, BigDecimal refundMoney) {
        appOrderShareProfitService.orderRefund(order, refundMoney);
    }


    @Override
    protected PushServiceImpl getPushService() {
        return appPushServiceImpl;
    }


    /**
     * 待接单倒计时
     *
     * @param payTime
     * @param beginTime
     * @return
     */
    private int receiveOrderTime(Date payTime, Date beginTime) {
        //todo 因为小程序没有提交开始时间 这里为了测试给一个开始时间
        if (beginTime == null) {
            beginTime = DateUtil.offsetMinute(payTime, 30);
        }
        int timeMinute = 15;
        Long minute = DateUtil.between(payTime, beginTime, DateUnit.MINUTE);
        if (minute < 15) {
            timeMinute = 15;
        } else {
            Integer.valueOf(minute + "");
        }
        return timeMinute;
    }

    /**
     * 待开始倒计时时长
     *
     * @param receivingTime
     * @param beginTime
     * @return
     */
    private int beginOrderTime(Date receivingTime, Date beginTime) {
        //todo 因为小程序没有提交开始时间 这里为了测试给一个开始时间

        if (beginTime == null) {
            beginTime = DateUtil.offsetMinute(receivingTime, 30);
        }
        int timeMinute = 15;
        Long minute = DateUtil.between(receivingTime, beginTime, DateUnit.MINUTE);
        if (minute < 15) {
            timeMinute = 15;
        } else {
            Integer.valueOf(minute + "");
        }
        return timeMinute;
    }


    /**
     * 待支付倒计时时间
     *
     * @param orderTime
     * @param beginTime
     * @return
     */
    private int waitForPayTime(Date orderTime, Date beginTime) {
        //todo 因为小程序没有提交开始时间 这里为了测试给一个开始时间

        if (beginTime == null) {
            beginTime = DateUtil.offsetMinute(orderTime, 30);
        }

        int timeMinute = 30;
        Long minute = DateUtil.between(orderTime, beginTime, DateUnit.MINUTE);
        if (minute > 30) {
            timeMinute = 30;
        } else if (minute < 5) {
            timeMinute = 5;
        } else {
            Integer.valueOf(minute + "");
        }
        return timeMinute;
    }

}
