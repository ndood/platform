package com.fulu.game.core.service.impl;

import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.ProductException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.threadpool.SpringThreadPoolExecutor;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.OrderDao;
import com.fulu.game.core.dao.OrderEventDao;
import com.fulu.game.core.dao.OrderShareProfitDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.entity.vo.responseVO.OrderResVO;
import com.fulu.game.core.entity.vo.searchVO.OrderSearchVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.aop.UserScore;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.BeanUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.fulu.game.common.enums.OrderStatusEnum.NON_PAYMENT;

@Service
@Slf4j
public class OrderServiceImpl extends AbsCommonService<Order, Integer> implements OrderService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OrderProductService orderProductService;
    @Autowired
    private OrderMoneyDetailsService orderMoneyDetailsService;
    @Autowired
    private OrderDealService orderDealService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private UserService userService;
    @Autowired
    private PlatformMoneyDetailsService platformMoneyDetailsService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private WxTemplateMsgService wxTemplateMsgService;
    @Autowired
    private OrderMarketProductService orderMarketProductService;
    @Autowired
    private PriceFactorService priceFactorService;
    @Autowired
    private PilotOrderService pilotOrderService;
    @Autowired
    private OrderStatusDetailsService orderStatusDetailsService;
    @Autowired
    private OrderShareProfitService orderShareProfitService;
    @Autowired
    private OrderEventService orderEventService;
    @Autowired
    private UserCommentService userCommentService;
    @Autowired
    private OrderEventDao orderEventDao;
    @Autowired
    private OrderShareProfitDao orderShareProfitDao;
    @Autowired
    private OrderPointProductService orderPointProductService;
    @Autowired
    private UserContactService userContactService;
    @Autowired
    private SpringThreadPoolExecutor springThreadPoolExecutor;
    @Autowired
    private UserAutoReceiveOrderService userAutoReceiveOrderService;

    @Override
    public ICommonDao<Order, Integer> getDao() {
        return orderDao;
    }


    @Override
    public PageInfo<PointOrderDetailsVO> receivingPointOrderList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        Integer[] status = new Integer[]{OrderStatusEnum.WAIT_SERVICE.getStatus()};
        List<PointOrderDetailsVO> pointOrderDetailsVOS = orderDao.receivingPointOrderList(Arrays.asList(status));
        return new PageInfo<>(pointOrderDetailsVOS);
    }

    @Override
    public PageInfo<PointOrderDetailsVO> pointOrderList(Integer pageNum, Integer pageSize, Integer type) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        User user = userService.getCurrentUser();
        List<PointOrderDetailsVO> list = orderDao.pointOrderList(type, user.getId());
        for (PointOrderDetailsVO orderDetailsVO : list) {
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
        return new PageInfo<PointOrderDetailsVO>(list);
    }

    @Override
    public Integer countNewPointOrder(Date startDate) {
        OrderVO param = new OrderVO();
        Integer[] statusList = new Integer[]{OrderStatusEnum.WAIT_SERVICE.getStatus()};
        param.setType(OrderTypeEnum.POINT.getType());
        param.setStatusList(statusList);
        param.setStartTime(startDate);
        return orderDao.countByParameter(param);
    }

    public void pushToServiceOrderWxMessage(Order order, WechatTemplateMsgEnum wechatTemplateMsgEnum) {
        if (OrderTypeEnum.POINT.getType().equals(order.getType())) {
            if (order.getServiceUserId() != null) {
                wxTemplateMsgService.pushWechatTemplateMsg(order.getServiceUserId(), wechatTemplateMsgEnum.choice(WechatEcoEnum.POINT.getType()));
            }
        } else {
            wxTemplateMsgService.pushWechatTemplateMsg(order.getServiceUserId(), wechatTemplateMsgEnum);
        }
    }

    public void pushToUserOrderWxMessage(Order order, WechatTemplateMsgEnum wechatTemplateMsgEnum) {
        String orderStatus = OrderStatusEnum.getMsgByStatus(order.getStatus());
        if (OrderTypeEnum.POINT.getType().equals(order.getType())) {
            if (order.getServiceUserId() != null) {
                wxTemplateMsgService.pushWechatTemplateMsg(order.getUserId(), wechatTemplateMsgEnum.choice(WechatEcoEnum.POINT.getType()), orderStatus);
            }
        } else {
            wxTemplateMsgService.pushWechatTemplateMsg(order.getUserId(), wechatTemplateMsgEnum, orderStatus);
        }

    }

    @Override
    public PageInfo<OrderResVO> list(OrderSearchVO orderSearchVO, Integer pageNum, Integer pageSize, String orderBy) {
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "id DESC";
        }
        if (orderSearchVO.getUserMobile() != null) {
            User user = userService.findByMobile(orderSearchVO.getUserMobile());
            if (user == null) {
                throw new ServiceErrorException("用户手机号输入错误!");
            }
            orderSearchVO.setUserId(user.getId());
        }
        if (orderSearchVO.getServiceUserMobile() != null) {
            User user = userService.findByMobile(orderSearchVO.getServiceUserMobile());
            if (user == null) {
                throw new ServiceErrorException("陪玩师手机号输入错误!");
            }
            orderSearchVO.setServiceUserId(user.getId());
        }
        PageHelper.startPage(pageNum, pageSize, orderBy);
        Integer status = orderSearchVO.getStatus();
        Integer[] statusList = OrderStatusGroupEnum.getByValue(status);
        if (null != statusList && statusList.length > 0) {
            orderSearchVO.setStatusList(statusList);
        }
        List<OrderResVO> list = orderDao.list(orderSearchVO);
        for (OrderResVO orderResVO : list) {
            //添加订单投诉和验证信息
            OrderDealVO userOrderDealVO = orderDealService.findByUserAndOrderNo(orderResVO.getUserId(), orderResVO.getOrderNo());
            orderResVO.setUserOrderDeal(userOrderDealVO);
            OrderDealVO serviceUserOrderDealVO = orderDealService.findByUserAndOrderNo(orderResVO.getServiceUserId(), orderResVO.getOrderNo());
            orderResVO.setServerOrderDeal(serviceUserOrderDealVO);
            //添加用户和陪玩师信息
            User user = userService.findById(orderResVO.getUserId());
            orderResVO.setUser(user);
            User serviceUser = userService.findById(orderResVO.getServiceUserId());
            orderResVO.setServerUser(serviceUser);
            //添加订单商品信息
            OrderProduct orderProduct = orderProductService.findByOrderNo(orderResVO.getOrderNo());
            orderResVO.setOrderProduct(orderProduct);
//            OrderMarketProduct orderMarketProduct = orderMarketProductService.findByOrderNo(orderResVO.getOrderNo());
//            orderResVO.setOrderMarketProduct(orderMarketProduct);
            //添加订单状态
            orderResVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderResVO.getStatus()));

            OrderShareProfitVO profitVO = new OrderShareProfitVO();
            profitVO.setOrderNo(orderResVO.getOrderNo());
            List<OrderShareProfit> profitList = orderShareProfitDao.findByParameter(profitVO);
            if (CollectionUtil.isEmpty(profitList)) {
                continue;
            }
            OrderShareProfit profit = profitList.get(0);
            BigDecimal commissionMoney = orderResVO.getCommissionMoney();
            BigDecimal serverMoney = orderResVO.getServerMoney();
            if (commissionMoney == null) {
                orderResVO.setCommissionMoney(profit.getCommissionMoney());
            }
            if (serverMoney == null) {
                orderResVO.setServerMoney(profit.getServerMoney());
            }
        }
        return new PageInfo(list);
    }


    @Override
    public PageInfo<OrderDetailsVO> list(int pageNum, int pageSize, Integer type) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        User user = userService.getCurrentUser();
        List<OrderDetailsVO> list = orderDao.listOrderDetails(type, user.getId());
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
        return new PageInfo<OrderDetailsVO>(list);
    }


    @Override
    public PageInfo<OrderVO> userList(int pageNum, int pageSize, Integer categoryId, Integer[] statusArr) {
        User user = userService.getCurrentUser();
        OrderVO params = new OrderVO();
        params.setUserId(user.getId());
        params.setCategoryId(categoryId);
        params.setStatusList(statusArr);
        PageHelper.startPage(pageNum, pageSize, "create_time desc");
        List<OrderVO> orderVOList = orderDao.findVOByParameter(params);
        for (OrderVO orderVO : orderVOList) {
            User server = userService.findById(orderVO.getServiceUserId());
            OrderProduct orderProduct = orderProductService.findByOrderNo(orderVO.getOrderNo());
            orderVO.setServerHeadUrl(server.getHeadPortraitsUrl());
            orderVO.setServerNickName(server.getNickname());
            orderVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderVO.getStatus()));
            orderVO.setServerScoreAvg(server.getScoreAvg() == null ? Constant.DEFAULT_SCORE_AVG : server.getScoreAvg());
            orderVO.setOrderProduct(orderProduct);
        }
        return new PageInfo<>(orderVOList);
    }

    @Override
    public PageInfo<OrderVO> serverList(int pageNum, int pageSize, Integer categoryId, Integer[] statusArr) {
        User user = userService.getCurrentUser();
        OrderVO params = new OrderVO();
        params.setServiceUserId(user.getId());
        params.setCategoryId(categoryId);
        params.setStatusList(statusArr);
        PageHelper.startPage(pageNum, pageSize, "create_time desc");
        List<OrderVO> orderVOList = orderDao.findVOByParameter(params);
        for (OrderVO orderVO : orderVOList) {
            Category category = categoryService.findById(orderVO.getCategoryId());
            orderVO.setCategoryIcon(category.getIcon());
            orderVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderVO.getStatus()));
        }
        return new PageInfo<>(orderVOList);
    }


    /**
     * 上分订单抢单
     *
     * @param orderNo
     * @return
     */
    @Override
    public String receivePointOrder(String orderNo, User serviceUser) {
        Order order = findByOrderNo(orderNo);
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
            update(order);
            //todo 设置多少分钟不能再自动派单
            log.info("抢单成功:userId:{};order:{}", serviceUser.getId(), order);
            //倒计时订单状态
            orderStatusDetailsService.create(orderNo, order.getStatus(), 10);
            //增加接单数量
            userAutoReceiveOrderService.addOrderNum(serviceUser.getId(), order.getCategoryId());
        } finally {
            redisOpenService.unlock(RedisKeyEnum.MARKET_ORDER_RECEIVE_LOCK.generateKey(order.getOrderNo()));
        }
        return orderNo;
    }


    public OrderDetailsVO findOrderDetails(String orderNo) {
        OrderDetailsVO orderDetailsVO = new OrderDetailsVO();

        User currentUser = userService.getCurrentUser();
        Order order = findByOrderNo(orderNo);
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

    @Override
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


    @Override
    public int count(Integer serverId, Integer[] statusList, Date startTime, Date endTime) {
        OrderVO params = new OrderVO();
        params.setServiceUserId(serverId);
        params.setStatusList(statusList);
        params.setStartTime(startTime);
        params.setEndTime(endTime);
        int count = orderDao.countByParameter(params);
        return count;
    }

    @Override
    public int weekOrderCount(Integer serverId) {
        Date startTime = DateUtil.beginOfWeek(new Date());
        Date endTime = DateUtil.endOfWeek(new Date());
        Integer[] statusList = OrderStatusGroupEnum.ALL_NORMAL_COMPLETE.getStatusList();
        return count(serverId, statusList, startTime, endTime);
    }

    @Override
    public int allOrderCount(Integer serverId) {
        Integer[] statusList = OrderStatusGroupEnum.ALL_NORMAL_COMPLETE.getStatusList();
        return count(serverId, statusList, null, null);
    }


    @Override
    public OrderDeal eventLeaveMessage(String orderNo, Integer eventId, String remark, String... fileUrl) {
        Order order = findByOrderNo(orderNo);
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


    @Override
    public String submit(int productId,
                         int num,
                         String remark,
                         String couponNo,
                         String userIp,
                         Integer contactType,
                         String contactInfo) {
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
        order.setType(OrderTypeEnum.PLATFORM.getType());
        order.setOrderNo(generateOrderNo());
        order.setUserId(user.getId());
        order.setServiceUserId(product.getUserId());
        order.setCategoryId(product.getCategoryId());
        order.setRemark(remark);
        order.setIsPay(false);
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
        //创建订单
        create(order);

        //保存联系方式
        userContactService.save(user.getId(), order.getContactType(), order.getContactInfo());

        //更新优惠券使用状态
        if (coupon != null) {
            couponService.updateCouponUseStatus(order.getOrderNo(), userIp, coupon);
        }
        //创建订单商品
        orderProductService.create(order, product, num);
        //计算订单状态倒计时24小时
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 24 * 60);
        return order.getOrderNo();
    }


    /**
     * 领航订单
     *
     * @param productId
     * @param num
     * @param remark
     * @param couponNo
     * @param userIp
     * @return
     */
    @Override
    public String pilotSubmit(int productId, int num, String remark, String couponNo, String userIp,
                              Integer contactType, String contactInfo) {
        log.info("领航用户提交订单productId:{};num:{};remark:{};couponNo:{};userIp:{};", productId, num, remark, couponNo, userIp);
        User user = userService.getCurrentUser();
        Product product = productService.findById(productId);
        if (product == null) {
            throw new ProductException(ProductException.ExceptionCode.PRODUCT_NOT_EXIST);
        }
        Category category = categoryService.findById(product.getCategoryId());
        //计算订单总价格
        BigDecimal totalMoney = product.getPrice().multiply(new BigDecimal(num));
        //计算领航订单金额
        PriceFactor priceFactor = priceFactorService.findByNewPriceFactor();
        BigDecimal pilotTotalMoney = priceFactor.getFactor().multiply(totalMoney);

        //创建订单
        Order order = new Order();
        order.setName(product.getProductName() + " " + num + "*" + product.getUnit());
        order.setType(OrderTypeEnum.PLATFORM.getType());
        order.setOrderNo(generateOrderNo());
        order.setUserId(user.getId());
        order.setServiceUserId(product.getUserId());
        order.setCategoryId(product.getCategoryId());
        order.setRemark(remark);
        order.setIsPay(false);
        order.setIsPayCallback(false);
        order.setTotalMoney(pilotTotalMoney);
        order.setActualMoney(pilotTotalMoney);
        order.setCharges(category.getCharges());
        order.setStatus(NON_PAYMENT.getStatus());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setOrderIp(userIp);
        order.setContactType(contactType);
        order.setContactInfo(contactInfo);
        if (order.getUserId().equals(order.getServiceUserId())) {
            throw new ServiceErrorException("不能给自己下单哦!");
        }
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
            couponService.updateCouponUseStatus(order.getOrderNo(), userIp, coupon);
        }
        //创建订单商品
        OrderProduct orderProduct = orderProductService.create(order, product, num);
        //新建领航订单数据
        PilotOrder pilotOrder = new PilotOrder();
        BeanUtil.copyProperties(order, pilotOrder);
        pilotOrder.setProductNum(orderProduct.getAmount());
        pilotOrder.setProductPrice(product.getPrice());
        pilotOrder.setPilotProductPrice(product.getPrice().multiply(priceFactor.getFactor()));
        pilotOrder.setFactor(priceFactor.getFactor());
        pilotOrder.setTotalMoney(totalMoney);
        pilotOrder.setPilotTotalMoney(pilotTotalMoney);
        pilotOrder.setSpreadMoney(pilotTotalMoney.subtract(totalMoney));
        pilotOrder.setIsComplete(false);
        pilotOrderService.create(pilotOrder);
        //计算订单状态倒计时24小时
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 24 * 60);
        return order.getOrderNo();
    }


    @Override
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
        orderPointProductVO.setOrderNo(order.getOrderNo());
        orderPointProductVO.setCreateTime(new Date());
        orderPointProductVO.setUpdateTime(new Date());
        orderPointProductService.create(orderPointProductVO);

        //计算订单状态倒计时24小时
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 10);
        //推送上分订单消息
        springThreadPoolExecutor.getAsyncExecutor().execute(new Runnable() {
            @Override
            public void run() {
                wxTemplateMsgService.pushPointOrder(order);
            }
        });

        return order.getOrderNo();
    }


    /**
     * 使用优惠券
     *
     * @return
     */
    public Coupon useCouponForOrder(String couponCode, Order order) {
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
        Order order = findByOrderNo(orderNo);
        if (order.getIsPay()) {
            throw new OrderException(orderNo, "重复支付订单![" + order.toString() + "]");
        }
        order.setIsPay(true);
        order.setIsPayCallback(true);
        order.setStatus(OrderStatusEnum.WAIT_SERVICE.getStatus());
        order.setUpdateTime(new Date());
        order.setPayTime(new Date());
        update(order);
        //记录平台流水
        platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.ORDER_PAY, order.getOrderNo(), order.getTotalMoney());
        if (order.getCouponNo() != null) {
            platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.COUPON_DEDUCTION, order.getOrderNo(), order.getCouponMoney().negate());
        }
        //如果订单类型是渠道商则扣渠道商款,如果是平台订单则发送通知
        if (OrderTypeEnum.POINT.getType().equals(order.getType())) {
            //订单状态倒计时
            orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 10);
            //记录订单流水
            orderMoneyDetailsService.create(order.getOrderNo(), order.getUserId(), DetailsEnum.ORDER_PAY, orderMoney);
            //通知
            wxTemplateMsgService.pushWechatTemplateMsg(order.getUserId(), WechatTemplateMsgEnum.POINT_TOSE_ORDER_RECEIVING);
        } else {
            //订单状态倒计时
            orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 24 * 60);
            //记录订单流水
            orderMoneyDetailsService.create(order.getOrderNo(), order.getUserId(), DetailsEnum.ORDER_PAY, orderMoney);
            //发送短信通知给陪玩师
            User server = userService.findById(order.getServiceUserId());
            SMSUtil.sendOrderReceivingRemind(server.getMobile(), order.getName());
            //推送通知
            pushToServiceOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOSERVICE_PAY);
        }
        return orderConvertVo(order);
    }

    /**
     * 陪玩师接单
     *
     * @return
     */
    @Override
    @UserScore(type = UserScoreEnum.ACCEPT_ORDER)
    public String serverReceiveOrder(String orderNo) {
        log.info("陪玩师接单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        userService.isCurrentUser(order.getServiceUserId());
        //只有等待陪玩和已支付的订单才能开始陪玩
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus()) || !order.getIsPay()) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, orderNo);
        }
        order.setStatus(OrderStatusEnum.ALREADY_RECEIVING.getStatus());
        order.setUpdateTime(new Date());
        order.setReceivingTime(new Date());
        update(order);
        //计算订单状态倒计时24小时
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 24 * 60);
        //推送通知
        pushToUserOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOUSER_AFFIRM_RECEIVE);
        return order.getOrderNo();
    }


    /**
     * 陪玩师开始服务
     *
     * @param orderNo
     * @return
     */
    @Override
    public String serverStartServeOrder(String orderNo) {
        log.info("陪玩师接单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        userService.isCurrentUser(order.getServiceUserId());
        if (!order.getStatus().equals(OrderStatusEnum.ALREADY_RECEIVING.getStatus()) &&
                !order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, orderNo);
        }
        order.setStatus(OrderStatusEnum.SERVICING.getStatus());
        order.setUpdateTime(new Date());
        update(order);
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        //推送通知
        pushToUserOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOUSER_START_SERVICE);
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
        Order order = findByOrderNo(orderNo);
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
        update(order);
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
        pushToServiceOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT);
        return orderNo;
    }


    @Override
    public OrderEventVO findOrderEvent(String orderNo) {
        Order order = findByOrderNo(orderNo);
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

    public List<OrderDealVO> adminFindOrderEvent(String orderNo, Integer orderEventType) {
        Order order = findByOrderNo(orderNo);

        OrderEventVO param = new OrderEventVO();
        param.setOrderNo(order.getOrderNo());
        param.setType(orderEventType);
        List<OrderEvent> orderEventList = orderEventDao.findByParameter(param);
        OrderEvent orderEvent;
        if (CollectionUtil.isEmpty(orderEventList)) {
            return null;
        } else {
            orderEvent = orderEventList.get(0);
        }

        List<OrderDealVO> orderDealVOList = orderDealService.findByOrderEventId(orderEvent.getId());
        if (CollectionUtil.isEmpty(orderDealVOList)) {
            return null;
        }
        for (OrderDealVO orderDealVO : orderDealVOList) {
            User ouser = userService.findById(orderDealVO.getUserId());
            if (ouser != null) {
                orderDealVO.setHeadUrl(ouser.getHeadPortraitsUrl());
                orderDealVO.setNickname(ouser.getNickname());
            }
        }
        return orderDealVOList;
    }

    @Override
    public List<OrderDealVO> findOrderConsultEvent(String orderNo) {
        return adminFindOrderEvent(orderNo, OrderEventTypeEnum.CONSULT.getType());
    }

    @Override
    public List<OrderDealVO> findNegotiateEvent(String orderNo) {
        return adminFindOrderEvent(orderNo, OrderEventTypeEnum.APPEAL.getType());
    }

    /**
     * 拒绝协商处理
     *
     * @param orderNo
     * @param orderConsultId
     * @param remark
     * @param fileUrls
     * @return
     */
    @Override
    public String consultRejectOrder(String orderNo,
                                     int orderConsultId,
                                     String remark,
                                     String[] fileUrls) {
        log.info("拒绝协商处理订单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        OrderEvent orderEvent = orderEventService.findById(orderConsultId);
        if (orderEvent == null || !order.getOrderNo().equals(orderEvent.getOrderNo())) {
            throw new OrderException(orderNo, "拒绝协商订单不匹配!");
        }
        User user = userService.getCurrentUser();
        userService.isCurrentUser(order.getServiceUserId());
        if (!order.getStatus().equals(OrderStatusEnum.CONSULTING.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, orderNo);
        }
        order.setStatus(OrderStatusEnum.CONSULT_REJECT.getStatus());
        order.setUpdateTime(new Date());
        update(order);
        OrderDeal orderDeal = new OrderDeal();
        orderDeal.setTitle("拒绝了协商");
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
        pushToUserOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOUSER_CONSULT_REJECT);
        return order.getOrderNo();
    }


    /**
     * 协商解决完成
     *
     * @param orderNo
     * @param orderEventId
     * @return
     */
    @Override
    @UserScore(type = UserScoreEnum.CONSULT)
    public String consultAgreeOrder(String orderNo, int orderEventId) {
        log.info("陪玩师同意协商处理订单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        OrderEvent orderEvent = orderEventService.findById(orderEventId);
        if (orderEvent == null || !order.getOrderNo().equals(orderEvent.getOrderNo())) {
            throw new OrderException(orderNo, "拒绝协商订单不匹配!");
        }
        User user = userService.getCurrentUser();
        userService.isCurrentUser(order.getServiceUserId());
        if (!order.getStatus().equals(OrderStatusEnum.CONSULTING.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, orderNo);
        }
        order.setStatus(OrderStatusEnum.CONSULT_COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        update(order);
        String title = "同意了协商，￥" + orderEvent.getRefundMoney().toPlainString() + "已经退款结算";
        OrderDeal orderDeal = new OrderDeal();
        orderDeal.setTitle(title);
        orderDeal.setType(OrderDealTypeEnum.CONSULT.getType());
        orderDeal.setUserId(user.getId());
        orderDeal.setRemark("陪玩师同意协商");
        orderDeal.setOrderNo(order.getOrderNo());
        orderDeal.setOrderEventId(orderEvent.getId());
        orderDeal.setCreateTime(new Date());
        orderDealService.create(orderDeal, null);
        //退款给用户
        orderShareProfitService.orderRefund(order, orderEvent.getRefundMoney());
        //创建订单状态详情
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        //推送通知同意协商
        pushToUserOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOUSER_CONSULT_AGREE);
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
        Order order = findByOrderNo(orderNo);
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
        update(order);
        log.info("取消协商处理更改订单状态后:{}", order);
        //推送通知
        pushToServiceOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT_CANCEL);
        return order.getOrderNo();
    }


    @Override
    public String systemConsultCancelOrder(String orderNo) {
        log.info("取消协商处理订单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        OrderEvent orderEvent = orderEventService.findByOrderNoAndType(orderNo, OrderEventTypeEnum.CONSULT.getType());
        if (orderEvent == null || !order.getOrderNo().equals(orderEvent.getOrderNo())) {
            throw new OrderException(orderNo, "拒绝协商订单不匹配!");
        }
        if (!order.getStatus().equals(OrderStatusEnum.CONSULT_REJECT.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, orderNo);
        }
        orderEventService.cancelConsult(order, null, orderEvent);
        log.info("取消协商处理更改订单状态前:{}", order);
        //订单状态重置,时间重置
        order.setStatus(orderEvent.getOrderStatus());
        order.setUpdateTime(new Date());
        update(order);
        log.info("取消协商处理更改订单状态后:{}", order);
        //推送通知
        pushToServiceOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOSERVICE_CONSULT_CANCEL);
        return order.getOrderNo();
    }

    @Override
    public String systemConsultAgreeOrder(String orderNo) {
        log.info("陪玩师同意协商处理订单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        OrderEvent orderEvent = orderEventService.findByOrderNoAndType(orderNo, OrderEventTypeEnum.CONSULT.getType());
        if (!order.getStatus().equals(OrderStatusEnum.CONSULTING.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, orderNo);
        }
        order.setStatus(OrderStatusEnum.SYSTEM_CONSULT_COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        update(order);
        String title = "系统自动同意了协商，￥" + orderEvent.getRefundMoney().toPlainString() + "已经退款结算";
        OrderDeal orderDeal = new OrderDeal();
        orderDeal.setTitle(title);
        orderDeal.setType(OrderDealTypeEnum.CONSULT.getType());
        orderDeal.setUserId(order.getServiceUserId());
        orderDeal.setRemark("系统自动同意协商");
        orderDeal.setOrderNo(order.getOrderNo());
        orderDeal.setOrderEventId(orderEvent.getId());
        orderDeal.setCreateTime(new Date());
        orderDealService.create(orderDeal);
        //创建订单状态详情
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        //推送通知同意协商
        pushToUserOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOUSER_CONSULT_AGREE);
        //退款给用户
        orderShareProfitService.orderRefund(order, orderEvent.getRefundMoney());
        return order.getOrderNo();
    }

    /**
     * 陪玩师取消订单
     *
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO serverCancelOrder(String orderNo) {
        log.info("陪玩师取消订单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        userService.isCurrentUser(order.getServiceUserId());
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, orderNo);
        }
        order.setStatus(OrderStatusEnum.SERVER_CANCEL.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        //消息提醒
        pushToUserOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOUSER_REJECT_RECEIVE);
        // 全额退款用户
        if (order.getIsPay()) {
            orderShareProfitService.orderRefund(order, order.getActualMoney());
        }
        return orderConvertVo(order);
    }

    /**
     * 系统取消订单
     *
     * @param orderNo
     */
    @Override
    public void systemCancelOrder(String orderNo) {
        Order order = findByOrderNo(orderNo);
        log.info("系统取消订单开始order:{}", order);
        if (!order.getStatus().equals(NON_PAYMENT.getStatus())
                && !order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())
                && !order.getStatus().equals(OrderStatusEnum.ALREADY_RECEIVING.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有等待陪玩和未支付的订单才能取消!");
        }
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        // 全额退款用户
        if (order.getIsPay()) {
            order.setStatus(OrderStatusEnum.SYSTEM_CLOSE.getStatus());
            orderShareProfitService.orderRefund(order, order.getActualMoney());
        } else {
            order.setStatus(OrderStatusEnum.UNPAY_ORDER_CLOSE.getStatus());
        }
        update(order);
        log.info("系统取消订单完成order:{}", order);
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
    }

    /**
     * 管理员取消订单
     *
     * @param orderNo
     */
    @Override
    public void adminCancelOrder(String orderNo) {
        Admin admin = adminService.getCurrentUser();
        log.info("管理员取消订单orderNo:{};admin:{};", orderNo, admin);
        Order order = findByOrderNo(orderNo);
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())
                && !order.getStatus().equals(OrderStatusEnum.SERVICING.getStatus())
                && !order.getStatus().equals(NON_PAYMENT.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有陪玩中和等待陪玩的订单才能取消!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_CLOSE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        // 全额退款用户
        if (order.getIsPay()) {
            orderShareProfitService.orderRefund(order, order.getActualMoney());
        }
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 0);
    }


    /**
     * 用户取消订单
     *
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO userCancelOrder(String orderNo) {
        log.info("用户取消订单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
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
        update(order);
        // 全额退款用户
        if (order.getIsPay()) {
            orderShareProfitService.orderRefund(order, order.getActualMoney());
            pushToServiceOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOSERVICE_ORDER_CANCEL);
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
        Order order = findByOrderNo(orderNo);
        User user = userService.getCurrentUser();
        if (!order.getStatus().equals(OrderStatusEnum.CONSULTING.getStatus())
                && !order.getStatus().equals(OrderStatusEnum.CONSULT_REJECT.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有协商中协商拒绝的订单才能申诉仲裁!");
        }
        order.setStatus(OrderStatusEnum.APPEALING.getStatus());
        order.setUpdateTime(new Date());
        update(order);
        //创建仲裁事件
        orderEventService.createAppeal(order, user, remark, fileUrl);
        //两小时倒计时
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 2 * 60);
        //推送通知
        if (user.getId().equals(order.getUserId())) {
            pushToServiceOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL);
        } else {
            pushToUserOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL);
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
        Order order = findByOrderNo(orderNo);
        userService.isCurrentUser(order.getServiceUserId());
        User user = userService.getCurrentUser();
        if (!order.getStatus().equals(OrderStatusEnum.SERVICING.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有陪玩中的订单才能验收!");
        }
        order.setStatus(OrderStatusEnum.CHECK.getStatus());
        order.setUpdateTime(new Date());
        update(order);
        //24小时自动验收
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 24 * 60);
        //提交验收订单
        orderEventService.createCheckEvent(order, user, remark, fileUrl);
        //推送通知
        pushToUserOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOUSER_CHECK);
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
        Order order = findByOrderNo(orderNo);
        userService.isCurrentUser(order.getUserId());
        if (!order.getStatus().equals(OrderStatusEnum.CHECK.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有待验收订单才能验收!");
        }
        order.setStatus(OrderStatusEnum.COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        //订单分润
        orderShareProfitService.shareProfit(order);
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        //确认服务
        pushToServiceOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOSERVICE_AFFIRM_SERVER);
        return orderConvertVo(order);
    }


    /**
     * 系统完成订单
     *
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO systemCompleteOrder(String orderNo) {
        Order order = findByOrderNo(orderNo);
        log.info("系统开始完成订单order:{}", order);
        if (!order.getStatus().equals(OrderStatusEnum.CHECK.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有待验收订单才能验收!");
        }
        order.setStatus(OrderStatusEnum.SYSTEM_COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        //订单分润
        orderShareProfitService.shareProfit(order);
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        log.info("系统完成订单order:{}", order);
        return orderConvertVo(order);
    }


    /**
     * 管理员强制完成订单 (打款给打手)
     *
     * @param orderNo
     * @return
     */
    public OrderVO adminHandleCompleteOrder(String orderNo) {
        Admin admin = adminService.getCurrentUser();
        log.info("管理员强制完成订单 (打款给打手)orderNo:{};adminId:{};adminName:{};", orderNo, admin.getId(), admin.getName());
        Order order = findByOrderNo(orderNo);
        if (!order.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有申诉中的订单才能操作!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);

        //仲裁留言
        OrderEvent orderEvent = orderEventService.findByOrderNoAndType(orderNo, OrderEventTypeEnum.APPEAL.getType());
        if (orderEvent != null) {
            OrderDeal orderDeal = new OrderDeal();
            orderDeal.setOrderNo(orderNo);
            orderDeal.setRemark("客服人员仲裁订单，结果:陪玩师胜诉");
            orderDeal.setTitle("客服人员仲裁订单");
            orderDeal.setType(OrderEventTypeEnum.APPEAL.getType());
            orderDeal.setOrderEventId(orderEvent.getId());
            orderDealService.create(orderDeal);
        }
        pushToServiceOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_SERVICE_WIN);
        pushToUserOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_SERVICE_WIN);

        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        //订单分润
        orderShareProfitService.shareProfit(order);
        return orderConvertVo(order);
    }


    /**
     * 管理员退款用户
     *
     * @param orderNo
     * @return
     */
    @Override
    @UserScore(type = UserScoreEnum.FULL_RESTITUTION)
    public OrderVO adminHandleRefundOrder(String orderNo) {
        Admin admin = adminService.getCurrentUser();
        log.info("管理员退款用户orderNo:{};adminId:{};adminName:{};", orderNo, admin.getId(), admin.getName());
        Order order = findByOrderNo(orderNo);
        if (!order.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有申诉中的订单才能操作!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_REFUND.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);

        //仲裁留言
        OrderEvent orderEvent = orderEventService.findByOrderNoAndType(orderNo, OrderEventTypeEnum.APPEAL.getType());
        if (orderEvent != null) {
            OrderDeal orderDeal = new OrderDeal();
            orderDeal.setOrderNo(orderNo);
            orderDeal.setRemark("客服人员仲裁订单，结果:老板胜诉");
            orderDeal.setTitle("客服人员仲裁订单");
            orderDeal.setType(OrderEventTypeEnum.APPEAL.getType());
            orderDeal.setOrderEventId(orderEvent.getId());
            orderDealService.create(orderDeal);
        }
        pushToServiceOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOSERVICE_APPEAL_USER_WIN);
        pushToUserOrderWxMessage(order, WechatTemplateMsgEnum.ORDER_TOUSER_APPEAL_USER_WIN);
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus());
        if (order.getIsPay()) {
            orderShareProfitService.orderRefund(order, order.getActualMoney());
        }
        return orderConvertVo(order);
    }


    /**
     * 管理员协商处理订单
     *
     * @param details
     * @return
     */
    @Override
    @UserScore(type = UserScoreEnum.NEGOTIATE)
    public OrderVO adminHandleNegotiateOrder(ArbitrationDetails details) {
        Admin admin = adminService.getCurrentUser();
        String orderNo = details.getOrderNo();
        log.info("管理员协商处理订单orderNo:{};adminId:{};adminName:{};", orderNo, admin.getId(), admin.getName());
        Order order = findByOrderNo(orderNo);
        details.setUserId(order.getUserId());
        details.setServiceUserId(order.getServiceUserId());
        if (!order.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())
                && !order.getStatus().equals(OrderStatusEnum.APPEALING_ADMIN.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有申诉中的订单才能操作!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_NEGOTIATE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);

        //仲裁留言
        OrderEvent orderEvent = orderEventService.findByOrderNoAndType(orderNo, OrderEventTypeEnum.APPEAL.getType());
        if (orderEvent != null) {
            OrderDeal orderDeal = new OrderDeal();
            orderDeal.setOrderNo(orderNo);
            orderDeal.setRemark("客服人员仲裁订单，结果:协商处理。留言:" + details.getRemark());
            orderDeal.setTitle("客服人员仲裁订单");
            orderDeal.setType(OrderEventTypeEnum.APPEAL.getType());
            orderDeal.setOrderEventId(orderEvent.getId());
            orderDealService.create(orderDeal);
        }

        wxTemplateMsgService.pushWechatTemplateMsg(order.getServiceUserId(), WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE, details.getRemark());
        wxTemplateMsgService.pushWechatTemplateMsg(order.getUserId(), WechatTemplateMsgEnum.ORDER_SYSTEM_APPEAL_NEGOTIATE, details.getRemark());

        if (order.getIsPay()) {
            orderShareProfitService.orderRefundToUserAndServiceUser(order, details);
            orderStatusDetailsService.create(orderNo, order.getStatus());
        } else {
            throw new OrderException(order.getOrderNo(), "只有已付款的订单才能操作!");
        }
        return orderConvertVo(order);
    }


    public List<Order> findByStatusList(Integer[] statusList) {
        if (statusList == null) {
            return new ArrayList<>();
        }
        OrderVO param = new OrderVO();
        param.setStatusList(statusList);
        return orderDao.findByParameter(param);
    }

    @Override
    public List<Order> findBySearchVO(OrderSearchVO orderSearchVO) {
        Integer status = orderSearchVO.getStatus();
        Integer[] statusList = OrderStatusGroupEnum.getByValue(status);
        if (null != statusList && statusList.length > 0) {
            orderSearchVO.setStatusList(statusList);
        }
        return orderDao.findBySearchVO(orderSearchVO);
    }


    @Override
    public List<Order> findByStatusListAndType(Integer[] statusList, int type) {
        if (statusList == null) {
            return new ArrayList<>();
        }
        OrderVO param = new OrderVO();
        param.setStatusList(statusList);
        param.setType(type);
        return orderDao.findByParameter(param);
    }

    /**
     * 生成订单号
     *
     * @return
     */
    private String generateOrderNo() {
        String orderNo = GenIdUtil.GetOrderNo();
        if (findByOrderNo(orderNo) == null) {
            return orderNo;
        } else {
            return generateOrderNo();
        }
    }

    public Order findByOrderNo(String orderNo) {
        if (orderNo == null) {
            return null;
        }
        Order order = orderDao.findByOrderNo(orderNo);
        return order;
    }

    @Override
    public Boolean isOldUser(Integer userId) {
        OrderVO orderVO = new OrderVO();
        orderVO.setUserId(userId);
        return orderDao.countByParameter(orderVO) > 0;
    }

    private OrderVO orderConvertVo(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtil.copyProperties(order, orderVO);
        return orderVO;
    }

    @Override
    public List<OrderStatusDetailsVO> getOrderProcess(String orderNo) {
        List<OrderStatusDetails> detailsList = orderStatusDetailsService.findOrderProcess(orderNo);
        if (CollectionUtil.isEmpty(detailsList)) {
            return null;
        }
        List<OrderStatusDetailsVO> voList = new ArrayList<>();
        for (OrderStatusDetails details : detailsList) {
            String msg = OrderStatusEnum.getMsgByStatus(details.getOrderStatus());
            OrderStatusDetailsVO vo = new OrderStatusDetailsVO();
            vo.setOrderNo(orderNo);
            vo.setCreateTime(details.getCreateTime());
            vo.setOrderStatus(details.getOrderStatus());
            vo.setOrderStatusMsg(msg);
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public UserAutoReceiveOrderVO getDynamicOrderInfo(Integer userId, Integer categoryId) {
        OrderVO orderVO = new OrderVO();
        orderVO.setUserId(userId);
        List<Order> orderList = orderDao.findByParameter(orderVO);
        if (CollectionUtil.isEmpty(orderList)) {
            return null;
        }
        int runningOrderNum = 0;
        for (Order meta : orderList) {
            boolean flag = meta.getStatus().equals(OrderStatusEnum.ALREADY_RECEIVING.getStatus())
                    || meta.getStatus().equals(OrderStatusEnum.SERVICING.getStatus())
                    || meta.getStatus().equals(OrderStatusEnum.CHECK.getStatus())
                    || meta.getStatus().equals(OrderStatusEnum.CONSULTING.getStatus())
                    || meta.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())
                    || meta.getStatus().equals(OrderStatusEnum.APPEALING_ADMIN.getStatus());
            if (flag) {
                runningOrderNum++;
            }
        }

        UserAutoReceiveOrder autoReceiveOrder = userAutoReceiveOrderService.findByUserIdAndCategoryId(userId, categoryId);
        if (autoReceiveOrder == null) {
            return null;
        }
        BigDecimal orderFailureRate = new BigDecimal(
                (autoReceiveOrder.getOrderCancelNum() + autoReceiveOrder.getOrderDisputeNum())
                        / autoReceiveOrder.getOrderCompleteNum());

        UserAutoReceiveOrderVO resultVo = new UserAutoReceiveOrderVO();
        resultVo.setRunningOrderNum(runningOrderNum);
        resultVo.setOrderFailureRate(orderFailureRate);
        return resultVo;
    }

    @Override
    public PageInfo<OrderVO> unacceptOrderList(Integer pageNum, Integer pageSize, OrderSearchVO orderSearchVO) {
        String orderBy = orderSearchVO.getOrderBy();
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "create_time desc";
        }
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<Order> orderList = orderDao.unacceptOrderList(orderSearchVO);
        if (CollectionUtil.isEmpty(orderList)) {
            return null;
        }

        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order meta : orderList) {
            OrderVO orderVO = new OrderVO();
            BeanUtil.copyProperties(meta, orderVO);
            String orderNo = meta.getOrderNo();
            OrderPointProduct orderPointProduct = orderPointProductService.findByOrderNo(orderNo);
            if (orderPointProduct != null) {
                orderVO.setAccountInfo(orderPointProduct.getAccountInfo());
                orderVO.setOrderChoice(orderPointProduct.getOrderChoice());
                orderVO.setPointType(orderPointProduct.getPointType());
            }
            orderVOList.add(orderVO);
        }

        PageInfo page = new PageInfo(orderList);
        page.setList(orderVOList);
        return page;
    }
}
