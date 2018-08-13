package com.fulu.game.core.service.impl.order;

import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.common.enums.UserTypeEnum;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.threadpool.SpringThreadPoolExecutor;
import com.fulu.game.core.dao.OrderDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.OrderPointProductVO;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.entity.vo.PointOrderDetailsVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.OrderServiceImpl;
import com.fulu.game.core.service.impl.coupon.PointCouponServiceImpl;
import com.fulu.game.core.service.impl.profit.PointOrderShareProfitServiceImpl;
import com.fulu.game.core.service.impl.push.MiniAppPushServiceImpl;
import com.fulu.game.core.service.impl.push.PointMiniAppPushServiceImpl;
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
public class PointMiniAppOrderServiceImpl extends OrderServiceImpl {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private PointCouponServiceImpl couponService;
    @Autowired
    private OrderStatusDetailsService orderStatusDetailsService;
    @Autowired
    private UserCommentService userCommentService;
    @Autowired
    private OrderPointProductService orderPointProductService;
    @Autowired
    private UserContactService userContactService;
    @Autowired
    private SpringThreadPoolExecutor springThreadPoolExecutor;
    @Autowired
    private PointOrderShareProfitServiceImpl pointOrderShareProfitService;
    @Autowired
    private PointMiniAppPushServiceImpl pointMiniAppPushService;



    public PageInfo<PointOrderDetailsVO> receivingPointOrderList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        Integer[] status = new Integer[]{OrderStatusEnum.WAIT_SERVICE.getStatus()};
        List<PointOrderDetailsVO> pointOrderDetailsVOS = orderDao.receivingPointOrderList(Arrays.asList(status));
        return new PageInfo<>(pointOrderDetailsVOS);
    }

    public PageInfo<PointOrderDetailsVO> pointOrderList(Integer pageNum, Integer pageSize, Integer type) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        User user = userService.getCurrentUser();
        List<PointOrderDetailsVO> list = orderDao.pointOrderList(type, user.getId());
        for (PointOrderDetailsVO pointOrderDetailsVO : list) {
            if (user.getId().equals(pointOrderDetailsVO.getUserId())) {
                pointOrderDetailsVO.setIdentity(UserTypeEnum.GENERAL_USER.getType());
            } else {
                pointOrderDetailsVO.setIdentity(UserTypeEnum.ACCOMPANY_PLAYER.getType());
            }
            pointOrderDetailsVO.setStatusStr(OrderStatusEnum.getMsgByStatus(pointOrderDetailsVO.getStatus()));
            pointOrderDetailsVO.setStatusNote(OrderStatusEnum.getNoteByStatus(pointOrderDetailsVO.getStatus()));
            Long countDown = orderStatusDetailsService.getCountDown(pointOrderDetailsVO.getOrderNo(), pointOrderDetailsVO.getStatus());
            pointOrderDetailsVO.setCountDown(countDown);
        }
        return new PageInfo<>(list);
    }

    public Integer countNewPointOrder(Date startDate) {
        OrderVO param = new OrderVO();
        Integer[] statusList = new Integer[]{OrderStatusEnum.WAIT_SERVICE.getStatus()};
        param.setType(OrderTypeEnum.POINT.getType());
        param.setStatusList(statusList);
        param.setStartTime(startDate);
        return orderDao.countByParameter(param);
    }

    public PointOrderDetailsVO findPointOrderDetails(String orderNo) {
        PointOrderDetailsVO orderDetailsVO = new PointOrderDetailsVO();

        User currentUser = userService.getCurrentUser();
        Order order = findByOrderNo(orderNo);
        if (currentUser.getId().equals(order.getUserId())) {
            orderDetailsVO.setIdentity(UserTypeEnum.GENERAL_USER.getType());
        } else if (order.getServiceUserId().equals(currentUser.getId())) {
            orderDetailsVO.setIdentity(UserTypeEnum.ACCOMPANY_PLAYER.getType());
        } else {
            throw new ServiceErrorException("用户不匹配!");
        }
        OrderPointProduct orderProduct = orderPointProductService.findByOrderNo(orderNo);
        BeanUtil.copyProperties(order, orderDetailsVO);

        orderDetailsVO.setAccountInfo(orderProduct.getAccountInfo());
        orderDetailsVO.setOrderChoice(orderProduct.getOrderChoice());
        orderDetailsVO.setCategoryIcon(orderProduct.getCategoryIcon());
        orderDetailsVO.setCategoryName(orderProduct.getCategoryName());

        List<Integer> invisibleContactList = Arrays.asList(OrderStatusGroupEnum.ORDER_CONTACT_INVISIBLE.getStatusList());
        if (invisibleContactList.contains(order.getStatus())) {
            orderDetailsVO.setContactInfo(null);
        }

        orderDetailsVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderDetailsVO.getStatus()));
        orderDetailsVO.setStatusNote(OrderStatusEnum.getNoteByStatus(orderDetailsVO.getStatus()));

        if (order.getServiceUserId() != null) {
            User server = userService.findById(order.getServiceUserId());
            orderDetailsVO.setServerHeadUrl(server.getHeadPortraitsUrl());
            orderDetailsVO.setServerNickName(server.getNickname());
        }


        User user = userService.findById(order.getUserId());
        orderDetailsVO.setUserHeadUrl(user.getHeadPortraitsUrl());
        orderDetailsVO.setUserNickName(user.getNickname());

        //orderStatus
        long countDown = orderStatusDetailsService.getCountDown(orderNo, order.getStatus());
        orderDetailsVO.setCountDown(countDown);


        //用户评论
        UserComment userComment = userCommentService.findByOrderNo(orderNo);
        if (userComment != null) {
            orderDetailsVO.setCommentContent(userComment.getContent());
            orderDetailsVO.setCommentScore(userComment.getScore());
        }
        return orderDetailsVO;
    }

    /**
     * 提交上分订单
     *
     * @param orderPointProductVO
     * @param couponNo
     * @param contactType
     * @param contactInfo
     * @param orderIp
     * @return
     */
    public String submitPointOrder(OrderPointProductVO orderPointProductVO,
                                   String couponNo,
                                   Integer contactType,
                                   String contactInfo,
                                   String orderIp) {
        User user = userService.getCurrentUser();
        Category category = categoryService.findById(orderPointProductVO.getCategoryId());
        BigDecimal totalMoney = orderPointProductVO.getPrice().multiply(new BigDecimal(orderPointProductVO.getAmount()));
        //创建订单
        Order order = new Order();
        order.setName(orderPointProductVO.getCategoryName() + " " + orderPointProductVO.getOrderChoice());
        order.setType(OrderTypeEnum.POINT.getType());
        order.setOrderNo(generateOrderNo());
        order.setUserId(user.getId());
        order.setCategoryId(orderPointProductVO.getCategoryId());
        order.setIsPay(false);
        order.setIsPayCallback(false);
        order.setTotalMoney(totalMoney);
        order.setActualMoney(totalMoney);
        order.setStatus(OrderStatusEnum.NON_PAYMENT.getStatus());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setOrderIp(orderIp);
        order.setCharges(category.getCharges());
        order.setContactType(contactType);
        order.setContactInfo(contactInfo);
        //使用优惠券
        Coupon coupon = null;
        if (StringUtils.isNotBlank(couponNo)) {
            coupon = useCouponForOrder(couponNo, order);
            if (coupon == null) {
                throw new ServiceErrorException("该优惠券不能使用!");
            }
        }
        //创建订单
        create(order);
        //保存联系方式
        userContactService.save(user.getId(), order.getContactType(), order.getContactInfo());
        //更新优惠券使用状态
        if (coupon != null) {
            couponService.updateCouponUseStatus(order.getOrderNo(), orderIp, coupon);
        }
        orderPointProductVO.setOrderNo(order.getOrderNo());
        orderPointProductVO.setCreateTime(new Date());
        orderPointProductVO.setUpdateTime(new Date());
        orderPointProductService.create(orderPointProductVO);
        //计算订单状态倒计时十分钟
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 10);

        return order.getOrderNo();
    }

    @Override
    protected void dealOrderAfterPay(Order order) {
        //订单状态倒计时
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 10);
        //通知
        pointMiniAppPushService.orderPay(order);
        //推送上分订单消息
        springThreadPoolExecutor.getAsyncExecutor().execute(new Runnable() {
            @Override
            public void run() {
                pointMiniAppPushService.pushPointOrder(order);
            }
        });
    }

    @Override
    protected void shareProfit(Order order) {
        pointOrderShareProfitService.shareProfit(order);
    }

    @Override
    public void orderRefund(Order order, BigDecimal refundMoney) {
        pointOrderShareProfitService.orderRefund(order, refundMoney);
    }


    @Override
    protected MiniAppPushServiceImpl getMinAppPushService(){
        return pointMiniAppPushService;
    }



}
