package com.fulu.game.h5.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.ProductException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.core.dao.OrderDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.OrderDetailsVO;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.AbOrderOpenServiceImpl;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.core.service.impl.push.PushServiceImpl;
import com.fulu.game.core.service.impl.push.SMSPushServiceImpl;
import com.fulu.game.thirdparty.fenqile.service.FenqileSdkOrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.fulu.game.common.enums.OrderStatusEnum.NON_PAYMENT;

@Service
@Slf4j
public class H5OrderServiceImpl extends AbOrderOpenServiceImpl {

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserContactService userContactService;
    @Autowired
    private OrderProductService orderProductService;
    @Autowired
    private OrderStatusDetailsService orderStatusDetailsService;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private UserCommentService userCommentService;
    @Autowired
    private SMSPushServiceImpl smsPushService;
    @Autowired
    private H5OrderShareProfitServiceImpl h5OrderShareProfitService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CouponService couponService;
    @Autowired
    @Qualifier(value = "userInfoAuthServiceImpl")
    private UserInfoAuthService userInfoAuthService;
    @Autowired
    private OrderEventService orderEventService;
    @Autowired
    private OrderDealService orderDealService;
    @Autowired
    private FenqileOrderService fenqileOrderService;
    @Autowired
    private FenqileSdkOrderService fenqileSdkOrderService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private ImService imService;

    @Override
    protected PushServiceImpl getPushService() {
        return smsPushService;
    }

    @Override
    protected void dealOrderAfterPay(Order order) {
        //订单状态倒计时
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 24 * 60);
        //发送短信通知给陪玩师
        User server = userService.findById(order.getServiceUserId());
        UserInfoAuth userInfoAuth = userInfoAuthService.findByUserId(order.getServiceUserId());
        boolean vestFlag = false;
        boolean agentIm = false;
        if (userInfoAuth != null) {
            vestFlag = userInfoAuth.getVestFlag() == null ? false : userInfoAuth.getVestFlag();
            agentIm = userInfoAuth.getOpenSubstituteIm() == null ? false : userInfoAuth.getOpenSubstituteIm();
        }

        //保存陪玩师的未读订单信息

        JSONArray waitingReadOrderNo = null;

        String wronJsonStr = redisOpenService.get(RedisKeyEnum.USER_WAITING_READ_ORDER.generateKey(order.getServiceUserId()));

        if (StringUtils.isBlank(wronJsonStr)) {
            waitingReadOrderNo = new JSONArray();
        } else {
            waitingReadOrderNo = JSONObject.parseArray(wronJsonStr);
        }

        waitingReadOrderNo.add(order.getOrderNo());

        redisOpenService.set(RedisKeyEnum.USER_WAITING_READ_ORDER.generateKey(order.getServiceUserId()), waitingReadOrderNo.toJSONString());


        if (!vestFlag) {
            smsPushService.sendOrderReceivingRemind(server.getMobile(), order.getName());
            //推送通知
            getPushService().orderPay(order);
        }

        //判断陪玩师是否为马甲或者代聊陪玩师  如果是，则给陪玩师发送一条im消息告诉他被下单了
        if (vestFlag || agentIm) {
            log.info("系统提示：用户下了新订单, orderNo:{}", order.getOrderNo());
            User user = userService.findById(order.getUserId());
            Map<String, Object> extMap = new HashMap<>();
            extMap.put("msg", "系统提示：用户下了新订单");
            imService.sendMsgToImUser(new String[]{server.getImId()}, user.getImId(), Constant.SERVICE_USER_PAY_ORDER, extMap);
        }
    }


    @Override
    protected void shareProfit(Order order) {
        h5OrderShareProfitService.shareProfit(order);
        try {
            //订单完成通知分期乐完成订单
            FenqileOrder fenqileOrder = fenqileOrderService.findByOrderNo(order.getOrderNo());
            if (fenqileOrder == null) {
                log.info("没有对应的分期乐订单:order:{}", order);
            }
            fenqileSdkOrderService.completeFenqileOrder(order.getOrderNo(), fenqileOrder.getFenqileNo());
        } catch (Exception e) {
            log.error("订单分润异常", e);
        }

    }


    @Override
    protected void orderRefund(Order order, BigDecimal refundMoney) {
        h5OrderShareProfitService.orderRefund(order, refundMoney);
    }

    public String submit(Integer productId, Integer num, String couponNo, String userIp,
                         Integer contactType, String contactInfo) {

        log.info("用户提交分期乐订单productId:{},num:{}", productId, num);
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
        //分期乐订单属于陪玩订单
        order.setType(OrderTypeEnum.PLAY.getType());
        order.setOrderNo(generateOrderNo());
        order.setUserId(user.getId());
        order.setServiceUserId(product.getUserId());
        order.setPlatform(PlatformEcoEnum.FENQILE.getType());
        order.setCategoryId(product.getCategoryId());
        order.setIsPay(false);
        order.setPayment(PaymentEnum.FENQILE_PAY.getType());
        order.setIsPayCallback(false);
        order.setTotalMoney(totalMoney);
        order.setActualMoney(totalMoney);
        order.setStatus(OrderStatusEnum.NON_PAYMENT.getStatus());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setOrderIp(userIp);
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
        if (order.getUserId().equals(order.getServiceUserId())) {
            throw new ServiceErrorException("不能给自己下单哦!");
        }
        //若支付金额为小于等于0，设置支付类型为零钱
        if(totalMoney.compareTo(new BigDecimal("0")) != 1){
            order.setPayment(PaymentEnum.BALANCE_PAY.getType());
        }
        //创建订单
        orderService.create(order);

        //保存联系方式
        userContactService.save(user.getId(), order.getContactType(), order.getContactInfo());

        //更新优惠券使用状态
        if (coupon != null) {
            couponService.updateCouponUseStatus(order.getOrderNo(), userIp, coupon);
        }

        //新建分期乐订单数据
        FenqileOrder fenqileOrder = new FenqileOrder();
        fenqileOrder.setOrderNo(order.getOrderNo());
        fenqileOrder.setUpdateTime(new Date());
        fenqileOrder.setCreateTime(new Date());
        fenqileOrderService.create(fenqileOrder);

        //创建订单商品
        orderProductService.create(order, product, num);
        //计算订单状态倒计时24小时
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 24 * 60);
        return order.getOrderNo();
    }


    public PageInfo<OrderDetailsVO> list(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        User user = userService.getCurrentUser();
        List<OrderDetailsVO> list = orderDao.fenqileOrderList(user.getId());
        for (OrderDetailsVO orderDetailsVO : list) {
            if (user.getId().equals(orderDetailsVO.getUserId())) {
                orderDetailsVO.setIdentity(UserTypeEnum.GENERAL_USER.getType());
            } else {
                orderDetailsVO.setIdentity(UserTypeEnum.ACCOMPANY_PLAYER.getType());
            }
            orderDetailsVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderDetailsVO.getStatus()));
            orderDetailsVO.setStatusNote(OrderStatusEnum.getNoteByStatus(orderDetailsVO.getStatus()));
            Long countDown = orderStatusDetailsService.getCountDown(orderDetailsVO.getOrderNo(), orderDetailsVO.getStatus());
            orderDetailsVO.setCountDown(countDown);
        }
        return new PageInfo<>(list);
    }


    /**
     * 分期乐用户取消订单
     *
     * @param orderNo
     * @return
     */
    public String fenqileUserCancelOrder(String orderNo) {
        log.info("分期乐用户取消订单orderNo:{}", orderNo);
        Order order = orderService.findByOrderNo(orderNo);
        if (order.getStatus() < 200) {
            log.info("orderNo:{}该订单已经是取消状态", orderNo);
            return orderNo;
        }
        if (!order.getStatus().equals(NON_PAYMENT.getStatus())) {
            throw new OrderException(order.getOrderNo(), "未支付的订单才能取消!");
        }
        if (order.getIsPay()) {
            order.setStatus(OrderStatusEnum.USER_CANCEL.getStatus());
        } else {
            order.setStatus(OrderStatusEnum.UNPAY_ORDER_CLOSE.getStatus());
        }
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        orderService.update(order);
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        return orderNo;
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
        //todo 分期乐订单不能部分退款
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
        orderDeal.setType(OrderEventTypeEnum.CONSULT.getType());
        orderDeal.setUserId(user.getId());
        orderDeal.setRemark(remark);
        orderDeal.setOrderNo(order.getOrderNo());
        orderDeal.setOrderEventId(orderEvent.getId());
        orderDeal.setCreateTime(new Date());
        orderDealService.create(orderDeal, fileUrls);
        //倒计时24小时后处理
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 24 * 60);
        //推送通知
        getPushService().consult(order);
        return orderNo;
    }


    /**
     * 获取订单详情
     *
     * @param orderNo
     * @return
     */
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

        User user = userService.findById(order.getUserId());
        orderDetailsVO.setUserHeadUrl(user.getHeadPortraitsUrl());
        orderDetailsVO.setUserNickName(user.getNickname());

        //orderStatus
        long countDown = orderStatusDetailsService.getCountDown(orderNo, order.getStatus());
        orderDetailsVO.setCountDown(countDown);
        orderDetailsVO.setProductId(orderProduct.getProductId());

        //用户评论
        UserComment userComment = userCommentService.findByOrderNo(orderNo);
        if (userComment != null) {
            orderDetailsVO.setCommentContent(userComment.getContent());
            orderDetailsVO.setCommentScore(userComment.getScore());
        }
        return orderDetailsVO;
    }

    public OrderVO getThunderOrderInfo() {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }

        OrderVO paramOrderVO = new OrderVO();
        paramOrderVO.setUserId(user.getId());
        paramOrderVO.setPlatform(PlatformEcoEnum.THUNDER.getType());
        paramOrderVO.setStatusList(OrderStatusGroupEnum.ADMIN_COMPLETE.getStatusList());

        OrderVO orderVO = orderDao.findOrderSum(paramOrderVO);
        BigDecimal userConsumeMoney = orderVO.getSumActualMoney().subtract(orderVO.getSumUserMoney()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        if (userConsumeMoney.compareTo(BigDecimal.ZERO) < 0) {
            log.info("迅雷订单数据异常，累计实付金额：{}，累计退款用户金额：{}", orderVO.getSumActualMoney(), orderVO.getSumUserMoney());
            userConsumeMoney = BigDecimal.ZERO;
        }

        orderVO.setUserConsumeMoney(userConsumeMoney);
        return orderVO;
    }
}
