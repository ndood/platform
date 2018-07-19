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
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.entity.vo.responseVO.OrderResVO;
import com.fulu.game.core.entity.vo.searchVO.OrderSearchVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Objects;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.BeanUtil;
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
    private CdkService cdkService;
    @Autowired
    private ChannelCashDetailsService channelCashDetailsService;
    @Autowired
    private PriceFactorService priceFactorService;
    @Autowired
    private PilotOrderService pilotOrderService;
    @Autowired
    private SpringThreadPoolExecutor springThreadPoolExecutor;
    @Autowired
    private OrderStatusDetailsService orderStatusDetailsService;
    @Autowired
    private OrderShareProfitService orderShareProfitService;
    @Autowired
    private OrderEventService orderEventService;
    @Autowired
    private UserCommentService userCommentService;





    @Override
    public ICommonDao<Order, Integer> getDao() {
        return orderDao;
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
            OrderMarketProduct orderMarketProduct = orderMarketProductService.findByOrderNo(orderResVO.getOrderNo());
            orderResVO.setOrderMarketProduct(orderMarketProduct);
            //添加订单状态
            orderResVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderResVO.getStatus()));
        }
        return new PageInfo(list);
    }



    @Override
    public PageInfo<OrderDetailsVO> list(int pageNum, int pageSize, Integer type) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        User user = userService.getCurrentUser();
        List<OrderDetailsVO> list = orderDao.listOrderDetails(type,user.getId());
        for(OrderDetailsVO orderDetailsVO : list){
            if(user.getId().equals(orderDetailsVO.getUserId())){
                orderDetailsVO.setIdentity(UserTypeEnum.GENERAL_USER.getType());
            }else{
                orderDetailsVO.setIdentity(UserTypeEnum.ACCOMPANY_PLAYER.getType());
            }
            orderDetailsVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderDetailsVO.getStatus()));
            orderDetailsVO.setStatusNote(OrderStatusEnum.getNoteByStatus(orderDetailsVO.getStatus()));
            Long countDown = orderStatusDetailsService.getCountDown(orderDetailsVO.getOrderNo(),orderDetailsVO.getStatus());
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
     * 集市订单列表
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @param statusArr
     * @return
     */
    @Override
    public PageInfo<MarketOrderVO> marketList(int pageNum, int pageSize, Integer categoryId, Integer[] statusArr) {
        OrderVO params = new OrderVO();
        params.setCategoryId(categoryId);
        params.setStatusList(statusArr);
        params.setType(OrderTypeEnum.MARKET.getType());
        PageHelper.startPage(pageNum, pageSize, "status asc,create_time desc");
        List<MarketOrderVO> marketOrderVOList = orderDao.findMarketByParameter(params);
        for (MarketOrderVO marketOrderVO : marketOrderVOList) {
            Category category = categoryService.findById(marketOrderVO.getCategoryId());
            marketOrderVO.setCategoryIcon(category.getIcon());
            marketOrderVO.setStatusStr(OrderStatusEnum.getMsgByStatus(marketOrderVO.getStatus()));
            marketOrderVO.setRemark(null);
        }
        return new PageInfo<>(marketOrderVOList);
    }


    @Override
    public Order marketReceiveOrder(String orderNo) {
        User serviceUser = userService.getCurrentUser();
        Order order = findByOrderNo(orderNo);
        log.info("陪玩师抢单:userId:{};order:{}", serviceUser.getId(), order);
        if (!OrderTypeEnum.MARKET.getType().equals(order.getType())) {
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
            order.setStatus(OrderStatusEnum.SERVICING.getStatus());
            order.setUpdateTime(new Date());
            update(order);
            log.info("抢单成功:userId:{};order:{}", serviceUser.getId(), order);
        } finally {
            redisOpenService.unlock(RedisKeyEnum.MARKET_ORDER_RECEIVE_LOCK.generateKey(order.getOrderNo()));
        }
        return order;
    }



    public OrderDetailsVO findOrderDetails(String orderNo){
        OrderDetailsVO orderDetailsVO = new OrderDetailsVO();

        User currentUser = userService.getCurrentUser();
        Order order = findByOrderNo(orderNo);
        if(currentUser.getId().equals(order.getUserId())){
            orderDetailsVO.setIdentity(UserTypeEnum.GENERAL_USER.getType());
        }else if(order.getServiceUserId().equals(currentUser.getId())){
            orderDetailsVO.setIdentity(UserTypeEnum.ACCOMPANY_PLAYER.getType());
        }else{
            throw new ServiceErrorException("用户不匹配!");
        }
        OrderProduct orderProduct = orderProductService.findByOrderNo(orderNo);
        BeanUtil.copyProperties(order, orderDetailsVO);

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
        long countDown = orderStatusDetailsService.getCountDown(orderNo,order.getStatus());
        orderDetailsVO.setCountDown(countDown);
        orderDetailsVO.setProductId(orderProduct.getProductId());

        //用户评论
        UserComment userComment =userCommentService.findByOrderNo(orderNo);
        if(userComment!=null){
            orderDetailsVO.setCommentContent(userComment.getContent());
            orderDetailsVO.setCommentScore(userComment.getScore());
        }


        return orderDetailsVO;
    }




    @Override
    public OrderVO findUserOrderDetails(String orderNo) {
        Order order = findByOrderNo(orderNo);

        userService.isCurrentUser(order.getUserId());
        OrderVO orderVO = new OrderVO();
        BeanUtil.copyProperties(order, orderVO);
        Category category = categoryService.findById(orderVO.getCategoryId());
        orderVO.setCategoryIcon(category.getIcon());
        orderVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderVO.getStatus()));
        //添加陪玩师信息
        User server = userService.findById(order.getServiceUserId());
        orderVO.setServerHeadUrl(server.getHeadPortraitsUrl());
        orderVO.setCategoryName(category.getName());
        orderVO.setServerAge(server.getAge());
        orderVO.setServerGender(server.getGender());
        orderVO.setServerNickName(server.getNickname());
        orderVO.setServerScoreAvg(server.getScoreAvg() == null ? Constant.DEFAULT_SCORE_AVG : server.getScoreAvg());
        orderVO.setServerCity(server.getCity());
        //添加订单商品信息
        OrderProduct orderProduct = orderProductService.findByOrderNo(orderNo);
        orderVO.setOrderProduct(orderProduct);



        return orderVO;
    }

    @Override
    public OrderVO findServerOrderDetails(String orderNo) {
        Order order = findByOrderNo(orderNo);
        userService.isCurrentUser(order.getServiceUserId());
        OrderVO orderVO = new OrderVO();
        BeanUtil.copyProperties(order, orderVO);
        Category category = categoryService.findById(orderVO.getCategoryId());
        orderVO.setCategoryIcon(category.getIcon());
        orderVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderVO.getStatus()));
        orderVO.setCategoryName(category.getName());
        //如果是集市订单则没有商品信息
        if (Objects.equal(OrderTypeEnum.MARKET.getType(), orderVO.getType())) {
            List<Integer> visibleStatus = Arrays.asList(OrderStatusGroupEnum.MARKET_ORDER_REMARK_VISIBLE.getStatusList());
            if (!visibleStatus.contains(order.getStatus())) {
                orderVO.setRemark("");
            }
        } else {
            //添加用户信息
            User user = userService.findById(order.getUserId());
            orderVO.setUserHeadUrl(user.getHeadPortraitsUrl());
            orderVO.setUserNickName(user.getNickname());
            //添加订单商品信息
            OrderProduct orderProduct = orderProductService.findByOrderNo(orderNo);
            orderVO.setOrderProduct(orderProduct);
        }

        return orderVO;
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
    public int countByChannelId(Integer channelId) {
        OrderVO orderVO = new OrderVO();
        orderVO.setChannelId(channelId);
        orderVO.setType(OrderTypeEnum.MARKET.getType());
        return orderDao.countByParameter(orderVO);
    }

    @Override
    public int countByChannelIdSuccess(Integer channelId) {
        Integer[] statusList = OrderStatusGroupEnum.ALL_NORMAL_COMPLETE.getStatusList();
        OrderVO orderVO = new OrderVO();
        orderVO.setChannelId(channelId);
        orderVO.setType(OrderTypeEnum.MARKET.getType());
        orderVO.setStatusList(statusList);
        return orderDao.countByParameter(orderVO);
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
            coupon = useCouponForOrder(couponNo,order);
            if (coupon == null) {
                throw new ServiceErrorException("该优惠券不能使用!");
            }
        }
        if (order.getUserId().equals(order.getServiceUserId())) {
            throw new ServiceErrorException("不能给自己下单哦!");
        }
        //创建订单
        create(order);
        //更新优惠券使用状态
        if (coupon != null) {
            couponService.updateCouponUseStatus(order.getOrderNo(),userIp,coupon);
        }
        //创建订单商品
        orderProductService.create(order,product,num);
        //计算订单状态倒计时24小时
        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),24*60);
        return order.getOrderNo();
    }


    /**
     * 领航订单
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
            coupon = useCouponForOrder(couponNo,order);
            if (coupon == null) {
                throw new ServiceErrorException("该优惠券不能使用!");
            }
        }
        //创建订单
        create(order);
        //更新优惠券使用状态
        if (coupon != null) {
            couponService.updateCouponUseStatus(order.getOrderNo(),userIp,coupon);
        }
        //创建订单商品
        OrderProduct orderProduct = orderProductService.create(order,product,num);
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
        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),24*60);
        return order.getOrderNo();
    }

    /**
     * 提交集市订单
     * @param channelId
     * @param orderMarketProduct
     * @param remark
     * @param orderIp
     * @return
     */
    public String submitMarketOrder(int channelId,
                                    OrderMarketProduct orderMarketProduct,
                                    String remark,
                                    String orderIp,
                                    String series) {
        log.info("提交集市订单:channelId:{};orderMarketProduct:{};remark:{};orderIp:{}", channelId, orderMarketProduct, remark, orderIp);
        Category category = categoryService.findById(orderMarketProduct.getCategoryId());
        //计算订单总价格
        BigDecimal totalMoney = orderMarketProduct.getPrice().multiply(new BigDecimal(orderMarketProduct.getAmount()));
        //创建订单
        Order order = new Order();
        order.setName(orderMarketProduct.getProductName());
        order.setOrderNo(generateOrderNo());
        order.setCategoryId(orderMarketProduct.getCategoryId());
        order.setRemark(remark);
        order.setIsPay(false);
        order.setType(OrderTypeEnum.MARKET.getType());
        order.setChannelId(channelId);
        order.setTotalMoney(totalMoney);
        order.setActualMoney(totalMoney);
        order.setStatus(NON_PAYMENT.getStatus());
        order.setCreateTime(new Date());
        order.setCharges(category.getCharges());
        order.setUpdateTime(new Date());
        order.setOrderIp(orderIp);
        create(order);
        //更新集市订单商品
        orderMarketProduct.setCreateTime(new Date());
        orderMarketProduct.setUpdateTime(new Date());
        orderMarketProduct.setOrderNo(order.getOrderNo());
        orderMarketProductService.create(orderMarketProduct);
        log.info("创建集市订单完成:order:{};", order);
        //更新cdkey使用状态
        Cdk cdk = cdkService.findBySeries(series);
        if (cdk != null) {
            cdk.setOrderNo(order.getOrderNo());
            cdk.setIsUse(true);
            cdk.setUpdateTime(new Date());
            log.info("更新CDK使用状态cdk:{}", cdk);
            cdkService.update(cdk);
        }
        //订单支付,扣渠道商流水
        payOrder(order.getOrderNo(), order.getActualMoney());

        //推送集市订单给对应陪玩师
        springThreadPoolExecutor.getAsyncExecutor().execute(new Runnable() {
            @Override
            public void run() {
                wxTemplateMsgService.pushMarketOrder(order);
            }
        });
        //把订单缓存到redis里面
        try {
            redisOpenService.hset(RedisKeyEnum.MARKET_ORDER.generateKey(order.getOrderNo()), BeanUtil.beanToMap(order), Constant.TIME_HOUR_TWO);
        } catch (Exception e) {
            log.error("订单转map错误:order:{};msg:{};", order, e.getMessage());
        }
        return order.getOrderNo();
    }



    /**
     * 使用优惠券
     * @return
     */
    public Coupon useCouponForOrder(String couponCode,Order order) {
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
        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),24*60);
        //记录平台流水
        platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.ORDER_PAY, order.getOrderNo(), order.getTotalMoney());
        if (order.getCouponNo() != null) {
            platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.COUPON_DEDUCTION, order.getOrderNo(), order.getCouponMoney().negate());
        }
        //如果订单类型是渠道商则扣渠道商款,如果是平台订单则发送通知
        if (OrderTypeEnum.MARKET.getType().equals(order.getType())) {
            channelCashDetailsService.cutCash(order.getChannelId(), order.getActualMoney(), order.getOrderNo());
        } else {
            //记录订单流水
            orderMoneyDetailsService.create(order.getOrderNo(), order.getUserId(), DetailsEnum.ORDER_PAY, orderMoney);
            //发送短信通知给陪玩师
            User server = userService.findById(order.getServiceUserId());
            SMSUtil.sendOrderReceivingRemind(server.getMobile(), order.getName());
            User user = userService.findById(order.getUserId());
            //推送通知陪玩师
            wxTemplateMsgService.pushWechatTemplateMsg(server.getId(), WechatTemplateMsgEnum.ORDER_USER_PAY, user.getNickname(), order.getName());
        }
        return orderConvertVo(order);
    }

    /**
     * 陪玩师接单
     * @return
     */
    @Override
    public String serverReceiveOrder(String orderNo) {
        log.info("陪玩师接单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        userService.isCurrentUser(order.getServiceUserId());
        //只有等待陪玩和已支付的订单才能开始陪玩
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus()) || !order.getIsPay()) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES,orderNo);
        }
        order.setStatus(OrderStatusEnum.ALREADY_RECEIVING.getStatus());
        order.setUpdateTime(new Date());
        order.setReceivingTime(new Date());
        update(order);
        //计算订单状态倒计时24小时
        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),24*60);
        return order.getOrderNo();
    }




    /**
     * 陪玩师开始服务
     * @param orderNo
     * @return
     */
    @Override
    public String serverStartServeOrder(String orderNo) {
        log.info("陪玩师接单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        userService.isCurrentUser(order.getServiceUserId());
        if (!order.getStatus().equals(OrderStatusEnum.ALREADY_RECEIVING.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES,orderNo);
        }
        order.setStatus(OrderStatusEnum.SERVICING.getStatus());
        order.setUpdateTime(new Date());
        update(order);
        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),0);
        return order.getOrderNo();
    }


    /**
     * 申请协商处理
     * @param orderNo
     * @param refundMoney
     * @param remark
     * @param fileUrls
     * @return
     */
    @Override
    public String userConsultOrder(String orderNo, BigDecimal refundMoney, String remark, String[] fileUrls) {
        log.info("用户申诉订单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        if(refundMoney==null){
            throw new OrderException(orderNo,"协商处理金额不能为空!");
        }
        String refundType = "";
        if(refundMoney.compareTo(order.getActualMoney())>0){
            throw new OrderException(orderNo,"协商处理金额不能大于订单支付金额!");
        }
        if(refundMoney.compareTo(order.getActualMoney())==0){
            refundType = "全部退款";
        }else{
            refundType = "部分退款";
        }
        User user = userService.getCurrentUser();
        userService.isCurrentUser(order.getUserId());
        if (!order.getStatus().equals(OrderStatusEnum.ALREADY_RECEIVING.getStatus())&&
            !order.getStatus().equals(OrderStatusEnum.SERVICING.getStatus())&&
            !order.getStatus().equals(OrderStatusEnum.CHECK.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES,orderNo);
        }
        //提交申诉
        OrderEvent orderEvent = orderEventService.createConsult(order,user,order.getStatus(),refundMoney);
        order.setStatus(OrderStatusEnum.CONSULTING.getStatus());
        order.setUpdateTime(new Date());
        update(order);
        //提交协商处理
        String title = "发起了协商-"+refundType+" ￥"+refundMoney.toPlainString();
        OrderDeal orderDeal = new OrderDeal();
        orderDeal.setTitle(title);
        orderDeal.setType(OrderDealTypeEnum.CONSULT.getType());
        orderDeal.setUserId(user.getId());
        orderDeal.setRemark(remark);
        orderDeal.setOrderNo(order.getOrderNo());
        orderDeal.setOrderEventId(orderEvent.getId());
        orderDeal.setCreateTime(new Date());
        orderDealService.create(orderDeal,fileUrls);

        //倒计时24小时后处理
        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),24*60);
        return order.getOrderNo();
    }



    @Override
    public OrderEventVO findOrderEvent(String orderNo) {
        Order order = findByOrderNo(orderNo);
        int type = OrderEventTypeEnum.CONSULT.getType();
        if( Arrays.asList(OrderStatusGroupEnum.CONSULT_ALL.getStatusList()).contains(order.getStatus())){
            type = OrderEventTypeEnum.CONSULT.getType();
        }else if(Arrays.asList(OrderStatusGroupEnum.APPEAL_ALL.getStatusList()).contains(order.getStatus())){
            type = OrderEventTypeEnum.APPEAL.getType();
        }
        User user = userService.getCurrentUser();
        OrderEventVO orderEventVO =orderEventService.getOrderEvent(order,user,type);
        User currentUser = userService.getCurrentUser();
        if(currentUser.getId().equals(orderEventVO.getUserId())){
            orderEventVO.setIdentity(UserTypeEnum.GENERAL_USER.getType());
        }else if(currentUser.getId().equals(orderEventVO.getServiceUserId())){
            orderEventVO.setIdentity(UserTypeEnum.ACCOMPANY_PLAYER.getType());
        }else{
            throw new ServiceErrorException("用户不匹配!");
        }
        return orderEventVO;
    }

    /**
     * 拒绝协商处理
     * @param orderNo
     * @param orderConsultId
     * @param remark
     * @param fileUrls
     * @return
     */
    @Override
    public String serverRejectConsultOrder(String orderNo,
                                           int orderConsultId,
                                           String remark,
                                           String[] fileUrls) {
        log.info("拒绝协商处理订单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        OrderEvent orderEvent = orderEventService.findById(orderConsultId);
        if(orderEvent==null||!order.getOrderNo().equals(orderEvent.getOrderNo())){
            throw new OrderException(orderNo,"拒绝协商订单不匹配!");
        }
        User user = userService.getCurrentUser();
        userService.isCurrentUser(order.getServiceUserId());
        if (!order.getStatus().equals(OrderStatusEnum.CONSULTING.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES,orderNo);
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
        orderDealService.create(orderDeal,fileUrls);
        //倒计时24小时后处理
        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),24*60);
        return order.getOrderNo();
    }


    /**
     * 协商解决完成
     * @param orderNo
     * @param orderConsultId
     * @param remark
     * @return
     */
    @Override
    public String serverAgreeConsultOrder(String orderNo, int orderConsultId, String remark) {
        log.info("陪玩师同意协商处理订单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        OrderEvent orderEvent = orderEventService.findById(orderConsultId);
        if(orderEvent==null||!order.getOrderNo().equals(orderEvent.getOrderNo())){
            throw new OrderException(orderNo,"拒绝协商订单不匹配!");
        }
        User user = userService.getCurrentUser();
        userService.isCurrentUser(order.getServiceUserId());
        if (!order.getStatus().equals(OrderStatusEnum.CONSULTING.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES,orderNo);
        }
        order.setStatus(OrderStatusEnum.CONSULT_COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        update(order);

        String title = "同意了协商，￥"+orderEvent.getRefundMoney().toPlainString()+"已经退款结算";
        OrderDeal orderDeal = new OrderDeal();
        orderDeal.setTitle(title);
        orderDeal.setType(OrderDealTypeEnum.CONSULT.getType());
        orderDeal.setUserId(user.getId());
        orderDeal.setRemark(remark);
        orderDeal.setOrderNo(order.getOrderNo());
        orderDeal.setOrderEventId(orderEvent.getId());
        orderDeal.setCreateTime(new Date());
        orderDealService.create(orderDeal,null);
        //退款给用户
        orderShareProfitService.orderRefund(order,orderEvent.getRefundMoney());
        //创建订单状态详情
        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),0);
        return order.getOrderNo();
    }


    /**
     * 用户取消协商
     * @param orderNo
     * @param orderConsultId
     * @return
     */
    @Override
    public String cancelConsultOrder(String orderNo, int orderConsultId) {
        log.info("取消协商处理订单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        OrderEvent orderEvent = orderEventService.findById(orderConsultId);
        if(orderEvent==null||!order.getOrderNo().equals(orderEvent.getOrderNo())){
            throw new OrderException(orderNo,"拒绝协商订单不匹配!");
        }
        User user = userService.getCurrentUser();
        userService.isCurrentUser(order.getUserId());
        if (!order.getStatus().equals(OrderStatusEnum.CONSULTING.getStatus())
             &&order.getStatus().equals(OrderStatusEnum.CONSULT_REJECT.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES,orderNo);
        }
        orderEventService.cancelConsult(order,user,orderEvent);
        log.info("取消协商处理更改订单状态前:{}",order);
        //订单状态重置,时间重置
        order.setStatus(orderEvent.getOrderStatus());
        order.setUpdateTime(new Date());
        update(order);
        log.info("取消协商处理更改订单状态后:{}",order);
        return order.getOrderNo();
    }

    /**
     * 陪玩师取消订单
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO serverCancelOrder(String orderNo) {
        log.info("陪玩师取消订单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        userService.isCurrentUser(order.getServiceUserId());
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES,orderNo);
        }
        order.setStatus(OrderStatusEnum.SERVER_CANCEL.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        // 全额退款用户
        if (order.getIsPay()) {
            orderShareProfitService.orderRefund(order,order.getActualMoney());
        }
        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),0);

        return orderConvertVo(order);
    }

    /**
     * 系统取消订单
     * @param orderNo
     */
    @Override
    public void systemCancelOrder(String orderNo) {
        log.info("系统取消订单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        if (!order.getStatus().equals(NON_PAYMENT.getStatus())
                && !order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有等待陪玩和未支付的订单才能取消!");
        }
        order.setStatus(OrderStatusEnum.SYSTEM_CLOSE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        // 全额退款用户
        if (order.getIsPay()) {
            orderShareProfitService.orderRefund(order,order.getActualMoney());
        }
        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),0);

    }

    /**
     * 管理员取消订单
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
            orderShareProfitService.orderRefund(order,order.getActualMoney());
        }

        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),0);

    }

    /**
     * 用户取消订单
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
        order.setStatus(OrderStatusEnum.USER_CANCEL.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        // 全额退款用户
        if (order.getIsPay()) {
            orderShareProfitService.orderRefund(order,order.getActualMoney());
        }
        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),0);
        return orderConvertVo(order);
    }


    /**
     * 用户仲裁订单
     * @param orderNo
     * @param remark
     * @param fileUrl
     * @return
     */
    @Override
    public OrderVO userAppealOrder(String orderNo,
                                   String remark,
                                   String... fileUrl) {
        log.info("用户申诉订单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        userService.isCurrentUser(order.getUserId());
        if (!order.getStatus().equals(OrderStatusEnum.SERVICING.getStatus()) && !order.getStatus().equals(OrderStatusEnum.CHECK.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有陪玩中和等待验收的订单才能申诉!");
        }
        order.setStatus(OrderStatusEnum.APPEALING.getStatus());
        order.setUpdateTime(new Date());
        update(order);

        //添加申诉文件
        orderDealService.create(orderNo, order.getUserId(), OrderDealTypeEnum.APPEAL.getType(), remark, fileUrl);
        //推送通知给双方
        if (order.getUserId() != null) {
            wxTemplateMsgService.pushWechatTemplateMsg(order.getUserId(), WechatTemplateMsgEnum.ORDER_USER_APPEAL);
        }
        wxTemplateMsgService.pushWechatTemplateMsg(order.getServiceUserId(), WechatTemplateMsgEnum.ORDER_SERVER_USER_APPEAL);
        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),0);
        return orderConvertVo(order);
    }


    /**
     * 管理员申诉订单
     * @param orderNo
     * @param remark
     * @return
     */
    @Override
    public OrderVO adminAppealOrder(String orderNo,
                                    String remark) {
        Admin admin = adminService.getCurrentUser();
        log.info("管理员申诉订单:orderNo:{};remark:{};admin:{};", orderNo, remark, admin);
        Order order = findByOrderNo(orderNo);
        if (!order.getStatus().equals(OrderStatusEnum.SERVICING.getStatus())
                && !order.getStatus().equals(OrderStatusEnum.CHECK.getStatus())
                && !order.getStatus().equals(NON_PAYMENT.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有陪玩中和等待验收的订单才能申诉!");
        }
        order.setStatus(OrderStatusEnum.APPEALING_ADMIN.getStatus());
        order.setUpdateTime(new Date());
        update(order);

        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),0);
        //添加申诉文本
        orderDealService.create(orderNo, order.getUserId(), OrderDealTypeEnum.APPEAL.getType(), remark);
        //推送通知给打手
        wxTemplateMsgService.pushWechatTemplateMsg(order.getServiceUserId(), WechatTemplateMsgEnum.ORDER_SERVER_USER_APPEAL);
        return orderConvertVo(order);
    }


    /**
     * 陪玩师提交验收订单
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO serverAcceptanceOrder(String orderNo,String remark,String[] fileUrl) {
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
        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),24*60);
        //提交验收订单
        orderEventService.createCheckEvent(order,user,remark,fileUrl);
        if (order.getUserId() != null) {
            wxTemplateMsgService.pushWechatTemplateMsg(order.getUserId(), WechatTemplateMsgEnum.ORDER_SERVER_USER_CHECK, order.getName());
        }
        return orderConvertVo(order);
    }

    /**
     * 用户验收订单
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
        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),0);
        return orderConvertVo(order);
    }


    /**
     * 系统完成订单
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO systemCompleteOrder(String orderNo) {
        log.info("系统完成订单orderNo:{}", orderNo);
        Order order = findByOrderNo(orderNo);
        if (!order.getStatus().equals(OrderStatusEnum.CHECK.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有待验收订单才能验收!");
        }
        order.setStatus(OrderStatusEnum.SYSTEM_COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        //订单分润
        orderShareProfitService.shareProfit(order);
        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),0);
        return orderConvertVo(order);
    }



    /**
     * 管理员强制完成订单 (打款给打手)
     * @param orderNo
     * @return
     */
    public OrderVO adminHandleCompleteOrder(String orderNo) {
        Admin admin = adminService.getCurrentUser();
        log.info("管理员强制完成订单 (打款给打手)orderNo:{};adminId:{};adminName:{};", orderNo, admin.getId(), admin.getName());
        Order order = findByOrderNo(orderNo);
        if (!order.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())
                && !order.getStatus().equals(OrderStatusEnum.APPEALING_ADMIN.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有申诉中的订单才能操作!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);

        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),0);
        //订单分润
        orderShareProfitService.shareProfit(order);
        if (order.getUserId() != null) {
            wxTemplateMsgService.pushWechatTemplateMsg(order.getUserId(), WechatTemplateMsgEnum.ORDER_USER_APPEAL_COMPLETE);
        }
        wxTemplateMsgService.pushWechatTemplateMsg(order.getServiceUserId(), WechatTemplateMsgEnum.ORDER_SERVER_USER_APPEAL_COMPLETE);
        return orderConvertVo(order);
    }

    /**
     * 管理员退款用户
     * @param orderNo
     * @return
     */
    public OrderVO adminHandleRefundOrder(String orderNo) {
        Admin admin = adminService.getCurrentUser();
        log.info("管理员退款用户orderNo:{};adminId:{};adminName:{};", orderNo, admin.getId(), admin.getName());
        Order order = findByOrderNo(orderNo);
        if (!order.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())
                && !order.getStatus().equals(OrderStatusEnum.APPEALING_ADMIN.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有申诉中的订单才能操作!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_REFUND.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        orderStatusDetailsService.create(order.getOrderNo(),order.getStatus(),0);
        if (order.getIsPay()) {
            orderShareProfitService.orderRefund(order,order.getActualMoney());
        }
        if (order.getUserId() != null) {
            wxTemplateMsgService.pushWechatTemplateMsg(order.getUserId(), WechatTemplateMsgEnum.ORDER_USER_APPEAL_REFUND);
        }
        wxTemplateMsgService.pushWechatTemplateMsg(order.getServiceUserId(), WechatTemplateMsgEnum.ORDER_SERVER_USER_APPEAL_REFUND);
        return orderConvertVo(order);
    }



    /**
     * 管理员协商处理订单
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO adminHandleNegotiateOrder(String orderNo) {
        Admin admin = adminService.getCurrentUser();
        log.info("管理员协商处理订单orderNo:{};adminId:{};adminName:{};", orderNo, admin.getId(), admin.getName());
        Order order = findByOrderNo(orderNo);
        if (!order.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())
                && !order.getStatus().equals(OrderStatusEnum.APPEALING_ADMIN.getStatus())) {
            throw new OrderException(order.getOrderNo(), "只有申诉中的订单才能操作!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_NEGOTIATE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        if (order.getUserId() != null) {
            wxTemplateMsgService.pushWechatTemplateMsg(order.getUserId(), WechatTemplateMsgEnum.ORDER_SYSTEM_USER_APPEAL_COMPLETE);
        }
        wxTemplateMsgService.pushWechatTemplateMsg(order.getServiceUserId(), WechatTemplateMsgEnum.ORDER_SYSTEM_SERVER_APPEAL_COMPLETE);
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
}
