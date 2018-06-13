package com.fulu.game.core.service.impl;

import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.ProductException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fulu.game.core.dao.OrderDao;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl extends AbsCommonService<Order,Integer> implements OrderService {

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
    private MoneyDetailsService moneyDetailsService;
    @Autowired
    private PlatformMoneyDetailsService platformMoneyDetailsService;
    @Autowired
    private PayService payService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private WxTemplateMsgService wxTemplateMsgService;
    @Autowired
    private OrderMarketProductService orderMarketProductService;


    @Override
    public ICommonDao<Order, Integer> getDao() {
        return orderDao;
    }

    @Override
    public PageInfo<OrderVO> userList(int pageNum, int pageSize, Integer categoryId, Integer[] statusArr) {
        User user = userService.getCurrentUser();
        OrderVO params = new OrderVO();
        params.setUserId(user.getId());
        params.setCategoryId(categoryId);
        params.setStatusList(statusArr);
        PageHelper.startPage(pageNum,pageSize,"create_time desc");
        List<OrderVO>  orderVOList = orderDao.findVOByParameter(params);
        for(OrderVO orderVO : orderVOList){
           User server = userService.findById(orderVO.getServiceUserId());
           OrderProduct orderProduct =  orderProductService.findByOrderNo(orderVO.getOrderNo());
           orderVO.setServerHeadUrl(server.getHeadPortraitsUrl());
           orderVO.setServerNickName(server.getNickname());
           orderVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderVO.getStatus()));
           orderVO.setServerScoreAvg(server.getScoreAvg()==null? Constant.DEFAULT_SCORE_AVG:server.getScoreAvg());
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
        PageHelper.startPage(pageNum,pageSize,"create_time desc");
        List<OrderVO>  orderVOList = orderDao.findVOByParameter(params);
        for(OrderVO orderVO : orderVOList){
            Category category = categoryService.findById(orderVO.getCategoryId());
            orderVO.setCategoryIcon(category.getIcon());
            orderVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderVO.getStatus()));
        }
        return new PageInfo<>(orderVOList);
    }

    @Override
    public OrderVO findUserOrderDetails(String orderNo){
        Order order = findByOrderNo(orderNo);
        userService.isCurrentUser(order.getUserId());
        OrderVO orderVO = new OrderVO();
        BeanUtil.copyProperties(order,orderVO);
        Category category = categoryService.findById(orderVO.getCategoryId());
        orderVO.setCategoryIcon(category.getIcon());
        orderVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderVO.getStatus()));
        //添加陪玩师信息
        User server= userService.findById(order.getServiceUserId());
        orderVO.setServerHeadUrl(server.getHeadPortraitsUrl());
        orderVO.setCategoryName(category.getName());
        orderVO.setServerAge(server.getAge());
        orderVO.setServerGender(server.getGender());
        orderVO.setServerNickName(server.getNickname());
        orderVO.setServerScoreAvg(server.getScoreAvg()==null? Constant.DEFAULT_SCORE_AVG:server.getScoreAvg());
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
        BeanUtil.copyProperties(order,orderVO);
        Category category = categoryService.findById(orderVO.getCategoryId());
        orderVO.setCategoryIcon(category.getIcon());
        orderVO.setStatusStr(OrderStatusEnum.getMsgByStatus(orderVO.getStatus()));
        orderVO.setCategoryName(category.getName());

        //添加用户信息
        User user= userService.findById(order.getUserId());
        orderVO.setUserHeadUrl(user.getHeadPortraitsUrl());
        orderVO.setUserNickName(user.getNickname());
        //添加订单商品信息
        OrderProduct orderProduct = orderProductService.findByOrderNo(orderNo);
        orderVO.setOrderProduct(orderProduct);

        return orderVO;
    }

    @Override
    public int count(Integer serverId,Integer[] statusList, Date startTime, Date endTime) {
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
        Date startTime =  DateUtil.beginOfWeek(new Date());
        Date endTime = DateUtil.endOfWeek(new Date());
        Integer[] statusList = OrderStatusGroupEnum.ALL_NORMAL_COMPLETE.getStatusList();
        return  count(serverId,statusList,startTime,endTime);
    }




    @Override
    public int allOrderCount(Integer serverId) {
        Integer[] statusList = OrderStatusGroupEnum.ALL_NORMAL_COMPLETE.getStatusList();
        return  count(serverId,statusList,null,null);
    }


    @Override
    public OrderVO submit(int productId,
                          int num,
                          String remark,
                          String couponNo,
                          String userIp) {
        log.info("用户提交订单productId:{},num:{},remark:{}",productId,num,remark);
        User user = userService.getCurrentUser();
        Product product = productService.findById(productId);
        if(product==null){
            throw new ProductException(ProductException.ExceptionCode.PRODUCT_NOT_EXIST);
        }
        Category category = categoryService.findById(product.getCategoryId());
        //计算订单总价格
        BigDecimal totalMoney = product.getPrice().multiply(new BigDecimal(num));
        //计算单笔订单佣金
        BigDecimal commissionMoney = totalMoney.multiply(category.getCharges());
        if(commissionMoney.compareTo(totalMoney)>0){
            throw new OrderException(category.getName(),"订单错误,佣金比订单总价高!");
        }
        //创建订单
        Order order = new Order();
        order.setName(product.getProductName()+" "+num+"*"+product.getUnit());
        order.setOrderNo(generateOrderNo());
        order.setUserId(user.getId());
        order.setServiceUserId(product.getUserId());
        order.setCategoryId(product.getCategoryId());
        order.setRemark(remark);
        order.setIsPay(false);
        order.setTotalMoney(totalMoney);
        order.setActualMoney(totalMoney);
        order.setStatus(OrderStatusEnum.NON_PAYMENT.getStatus());
        order.setCommissionMoney(commissionMoney);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        //使用优惠券
        Coupon coupon = null;
        if(StringUtils.isNotBlank(couponNo)){
            coupon = getCoupon(couponNo);
            if(coupon==null){
                throw new ServiceErrorException("该优惠券不能使用!");
            }
            order.setCouponNo(coupon.getCouponNo());
            order.setCouponMoney(coupon.getDeduction());
            //判断优惠券金额是否大于订单总额
            if(coupon.getDeduction().compareTo(order.getTotalMoney())>=0){
                order.setActualMoney(new BigDecimal(0));
                order.setCouponMoney(order.getTotalMoney());
            }else{
                BigDecimal actualMoney = order.getTotalMoney().subtract(coupon.getDeduction());
                order.setActualMoney(actualMoney);
            }
        }
        if(order.getUserId().equals(order.getServiceUserId())){
            throw new ServiceErrorException("不能给自己下单哦!");
        }
        //创建订单
        create(order);
        //更新优惠券使用状态
        if(coupon!=null){
            coupon.setOrderNo(order.getOrderNo());
            coupon.setIsUse(true);
            coupon.setUseTime(new Date());
            coupon.setUseIp(userIp);
            couponService.update(coupon);
        }
        //创建订单商品
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderNo(order.getOrderNo());
        orderProduct.setAmount(num);
        orderProduct.setUnit(product.getUnit());
        orderProduct.setPrice(product.getPrice());
        orderProduct.setProductId(product.getId());
        orderProduct.setProductName(order.getName());
        orderProduct.setCreateTime(new Date());
        orderProduct.setUpdateTime(new Date());
        orderProductService.create(orderProduct);
        OrderVO orderVO = new OrderVO();
        BeanUtil.copyProperties(order,orderVO);
        orderVO.setOrderProduct(orderProduct);
        return orderVO;
    }



    public String submitMarketOrder(int channelId,
                                    OrderMarketProduct orderMarketProduct,
                                    String remark,
                                    String orderIp){
        Category category = categoryService.findById(orderMarketProduct.getCategoryId());
        //计算订单总价格
        BigDecimal totalMoney = orderMarketProduct.getPrice().multiply(new BigDecimal(orderMarketProduct.getAmount()));
        //计算单笔订单佣金
        BigDecimal commissionMoney = totalMoney.multiply(category.getCharges());
        if(commissionMoney.compareTo(totalMoney)>0){
            throw new OrderException(category.getName(),"订单错误,佣金比订单总价高!");
        }
        //创建订单
        Order order = new Order();
        order.setName(orderMarketProduct.getProductName());
        order.setOrderNo(generateOrderNo());
        order.setCategoryId(orderMarketProduct.getCategoryId());
        order.setRemark(remark);
        order.setIsPay(true);
        order.setType(OrderTypeEnum.MARKET.getType());
        order.setChannelId(channelId);
        order.setTotalMoney(totalMoney);
        order.setActualMoney(totalMoney);
        order.setStatus(OrderStatusEnum.WAIT_SERVICE.getStatus());
        order.setCommissionMoney(commissionMoney);
        order.setPayTime(new Date());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setOrderIp(orderIp);
        create(order);
        //更新集市订单商品
        orderMarketProduct.setCreateTime(new Date());
        orderMarketProduct.setUpdateTime(new Date());
        orderMarketProduct.setOrderNo(order.getOrderNo());
        orderMarketProductService.create(orderMarketProduct);

        return order.getOrderNo();
    }

    /**
     * 使用优惠券
     * @return
     */
    public Coupon getCoupon(String couponCode){
        Coupon coupon =couponService.findByCouponNo(couponCode);
        //判断是否是自己的优惠券
        userService.isCurrentUser(coupon.getUserId());
        //判断该优惠券是否可用
        if(!couponService.couponIsAvailable(coupon)){
            return null;
        }
        return coupon;
    }


    /**
     * 订单支付
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO payOrder(String orderNo,BigDecimal orderMoney){
        log.info("用户支付订单orderNo:{},orderMoney:{}",orderNo,orderMoney);
        Order order =  findByOrderNo(orderNo);
        if(order.getIsPay()){
           throw new OrderException(orderNo,"重复支付订单!["+order.toString()+"]");
        }
        order.setIsPay(true);
        order.setStatus(OrderStatusEnum.WAIT_SERVICE.getStatus());
        order.setUpdateTime(new Date());
        order.setPayTime(new Date());
        update(order);
        //记录订单流水
        orderMoneyDetailsService.create(order.getOrderNo(),order.getUserId(), DetailsEnum.ORDER_PAY,orderMoney);
        //记录平台流水
        platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.ORDER_PAY,order.getOrderNo(),order.getTotalMoney());
        if(order.getCouponNo()!=null){
            platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.COUPON_DEDUCTION,order.getOrderNo(),order.getCouponMoney().negate());
        }
        //发送短信通知给陪玩师
        User server = userService.findById(order.getServiceUserId());
        SMSUtil.sendOrderReceivingRemind(server.getMobile(),order.getName());

        User user = userService.findById(order.getUserId());
        //推送通知陪玩师
        wxTemplateMsgService.pushWechatTemplateMsg(server.getId(),WechatTemplateMsgEnum.ORDER_USER_PAY,user.getNickname(),order.getName());

        return orderConvertVo(order);
    }


    /**
     * 陪玩师接单
     * @return
     */
    @Override
    public OrderVO serverReceiveOrder(String orderNo){
        log.info("陪玩师接单orderNo:{}",orderNo);
        Order order =  findByOrderNo(orderNo);
        userService.isCurrentUser(order.getServiceUserId());
        //只有等待陪玩和已支付的订单才能开始陪玩
        if(!order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())||!order.getIsPay()){
            throw new OrderException(order.getOrderNo(),"订单未支付或者状态不是等待陪玩!");
        }
        order.setStatus(OrderStatusEnum.SERVICING.getStatus());
        order.setUpdateTime(new Date());
        update(order);

        OrderProduct orderProduct = orderProductService.findByOrderNo(orderNo);
        //如果陪玩师开始接单缓存陪玩师开始服务状态 时间:商品数量*小时
        long expire = orderProduct.getAmount()*3600;
        redisOpenService.set(RedisKeyEnum.USER_ORDER_ALREADY_SERVICE_KEY.generateKey(order.getServiceUserId()), order.getOrderNo(),expire);
        return orderConvertVo(order);
    }



    /**
     * 陪玩师取消订单
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO serverCancelOrder(String orderNo){
        log.info("陪玩师取消订单orderNo:{}",orderNo);
        Order order =  findByOrderNo(orderNo);
        userService.isCurrentUser(order.getServiceUserId());
        if(!order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())
            &&!order.getStatus().equals(OrderStatusEnum.SERVICING.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有陪玩中和等待陪玩的订单才能取消!");
        }
        order.setStatus(OrderStatusEnum.SERVER_CANCEL.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        if(order.getIsPay()){
            // 全额退款用户
            orderRefund(order.getOrderNo(),order.getUserId(),order.getActualMoney());
        }
        return orderConvertVo(order);
    }


    @Override
    public OrderVO systemCancelOrder(String orderNo) {
        log.info("系统取消订单orderNo:{}",orderNo);
        Order order =  findByOrderNo(orderNo);
        order.setStatus(OrderStatusEnum.SYSTEM_CLOSE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        if(order.getIsPay()){
            // 全额退款用户
            orderRefund(order.getOrderNo(),order.getUserId(),order.getActualMoney());
        }
        return orderConvertVo(order);
    }

    /**
     * 用户取消订单
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO userCancelOrder(String orderNo) {
        log.info("用户取消订单orderNo:{}",orderNo);
        Order order =  findByOrderNo(orderNo);
        userService.isCurrentUser(order.getUserId());
        if(!order.getStatus().equals(OrderStatusEnum.NON_PAYMENT.getStatus())
            &&!order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有等待陪玩和未支付的订单才能取消!");
        }
        order.setStatus(OrderStatusEnum.USER_CANCEL.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        if(order.getIsPay()){
            // 全额退款用户
            orderRefund(order.getOrderNo(),order.getUserId(),order.getActualMoney());
        }
        return orderConvertVo(order);
    }


    /**
     * 用户申诉订单
     * @param orderNo
     * @param remark
     * @param fileUrl
     * @return
     */
    @Override
    public OrderVO userAppealOrder(String orderNo,
                                   String remark,
                                   String ... fileUrl){
        log.info("用户申诉订单orderNo:{}",orderNo);
        Order order =  findByOrderNo(orderNo);
        userService.isCurrentUser(order.getUserId());
        if(!order.getStatus().equals(OrderStatusEnum.SERVICING.getStatus())
            &&!order.getStatus().equals(OrderStatusEnum.CHECK.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有陪玩中和等待验收的订单才能申诉!");
        }
        order.setStatus(OrderStatusEnum.APPEALING.getStatus());
        order.setUpdateTime(new Date());
        update(order);
        //删除打手服务状态
        deleteAlreadyService(order.getServiceUserId());
        //添加申诉文件
        orderDealService.create(orderNo, order.getUserId(),OrderDealTypeEnum.APPEAL.getType(),remark,fileUrl);

        //推送通知给双方
        wxTemplateMsgService.pushWechatTemplateMsg(order.getUserId(),WechatTemplateMsgEnum.ORDER_USER_APPEAL);
        wxTemplateMsgService.pushWechatTemplateMsg(order.getUserId(),WechatTemplateMsgEnum.ORDER_SERVER_USER_APPEAL);

        return orderConvertVo(order);
    }


    /**
     * 打手提交验收订单
     * @param orderNo
     * @param remark
     * @param fileUrl
     * @return
     */
    @Override
    public OrderVO serverAcceptanceOrder(String orderNo, String remark, String ... fileUrl){
        log.info("打手提交验收订单orderNo:{}",orderNo);
        Order order =  findByOrderNo(orderNo);
        userService.isCurrentUser(order.getServiceUserId());
        if(!order.getStatus().equals(OrderStatusEnum.SERVICING.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有陪玩中的订单才能验收!");
        }
        order.setStatus(OrderStatusEnum.CHECK.getStatus());
        order.setUpdateTime(new Date());
        update(order);
        //添加验收文件
        orderDealService.create(orderNo, order.getServiceUserId(),OrderDealTypeEnum.CHECK.getType(),remark,fileUrl);
        deleteAlreadyService(order.getServiceUserId());

        wxTemplateMsgService.pushWechatTemplateMsg(order.getUserId(),WechatTemplateMsgEnum.ORDER_SERVER_USER_CHECK,order.getName());
        return orderConvertVo(order);
    }

    /**
     * 用户验收订单
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO userVerifyOrder(String orderNo){
        log.info("用户验收订单orderNo:{}",orderNo);
        Order order =  findByOrderNo(orderNo);
        userService.isCurrentUser(order.getUserId());
        if(!order.getStatus().equals(OrderStatusEnum.CHECK.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有待验收订单才能验收!");
        }
        order.setStatus(OrderStatusEnum.COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        //订单分润
        shareProfit(order);
        return orderConvertVo(order);
    }


    /**
     * 系统完成订单
     * @param orderNo
     * @return
     */
    @Override
    public OrderVO systemCompleteOrder(String orderNo){
        log.info("系统完成订单orderNo:{}",orderNo);
        Order order =  findByOrderNo(orderNo);
        if(!order.getStatus().equals(OrderStatusEnum.CHECK.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有待验收订单才能验收!");
        }
        order.setStatus(OrderStatusEnum.SYSTEM_COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        //订单分润
        shareProfit(order);
        return orderConvertVo(order);
    }


    /**
     * 订单分润
     * @param order
     */
    public void shareProfit(Order order){
        BigDecimal serverMoney = order.getTotalMoney().subtract(order.getCommissionMoney());
        //记录用户加零钱
        moneyDetailsService.orderSave(serverMoney,order.getServiceUserId(),order.getOrderNo());
        //平台记录支付打手流水
        platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.ORDER_SHARE_PROFIT,order.getOrderNo(),serverMoney.negate());
    }



    /**
     * 管理员强制完成订单 (打款给打手)
     * @param orderNo
     * @return
     */
    public OrderVO adminHandleCompleteOrder(String orderNo){
        Admin admin = adminService.getCurrentUser();
        log.info("管理员强制完成订单 (打款给打手)orderNo:{};adminId:{};adminName:{};",orderNo,admin.getId(),admin.getName());
        Order order =  findByOrderNo(orderNo);
        if(!order.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有申诉中的订单才能操作!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_COMPLETE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        //订单分润
        shareProfit(order);

        wxTemplateMsgService.pushWechatTemplateMsg(order.getUserId(),WechatTemplateMsgEnum.ORDER_USER_APPEAL_COMPLETE);
        wxTemplateMsgService.pushWechatTemplateMsg(order.getServiceUserId(),WechatTemplateMsgEnum.ORDER_SERVER_USER_APPEAL_COMPLETE);

        return orderConvertVo(order);
    }

    /**
     * 管理员退款用户
     * @param orderNo
     * @return
     */
    public OrderVO adminHandleRefundOrder(String orderNo){
        Admin admin = adminService.getCurrentUser();
        log.info("管理员退款用户orderNo:{};adminId:{};adminName:{};",orderNo,admin.getId(),admin.getName());
        Order order =  findByOrderNo(orderNo);
        if(!order.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有申诉中的订单才能操作!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_REFUND.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        update(order);
        if(order.getIsPay()){
            orderRefund(order.getOrderNo(),order.getUserId(),order.getActualMoney());
        }


        wxTemplateMsgService.pushWechatTemplateMsg(order.getUserId(),WechatTemplateMsgEnum.ORDER_USER_APPEAL_REFUND);
        wxTemplateMsgService.pushWechatTemplateMsg(order.getServiceUserId(),WechatTemplateMsgEnum.ORDER_SERVER_USER_APPEAL_REFUND);
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
        log.info("管理员协商处理订单orderNo:{};adminId:{};adminName:{};",orderNo,admin.getId(),admin.getName());
        Order order =  findByOrderNo(orderNo);
        if(!order.getStatus().equals(OrderStatusEnum.APPEALING.getStatus())){
            throw new OrderException(order.getOrderNo(),"只有申诉中的订单才能操作!");
        }
        order.setStatus(OrderStatusEnum.ADMIN_NEGOTIATE.getStatus());
        order.setUpdateTime(new Date());
        order.setCompleteTime(new Date());
        order.setCommissionMoney(order.getTotalMoney());
        update(order);
        //订单协商,全部金额记录平台流水
        wxTemplateMsgService.pushWechatTemplateMsg(order.getUserId(),WechatTemplateMsgEnum.ORDER_SYSTEM_USER_APPEAL_COMPLETE);
        wxTemplateMsgService.pushWechatTemplateMsg(order.getServiceUserId(),WechatTemplateMsgEnum.ORDER_SYSTEM_SERVER_APPEAL_COMPLETE);
        return orderConvertVo(order);
    }

    /**
     * 订单退款
     * @param orderNo
     * @param userId
     * @param orderMoney
     */
    public void orderRefund(String orderNo,Integer userId,BigDecimal orderMoney){
        Order order = findByOrderNo(orderNo);
        if(!order.getIsPay()){
            throw new OrderException(orderNo,"未支付订单不允许退款!");
        }
        try {
            payService.refund(orderNo,orderMoney);
        }catch (Exception e){
            log.error("退款失败{}",orderNo,e.getMessage());
            throw new OrderException(orderNo,"订单退款失败!");
        }
        //记录订单流水
        orderMoneyDetailsService.create(orderNo,userId,DetailsEnum.ORDER_USER_CANCEL,orderMoney.negate());
        //记录平台流水
        platformMoneyDetailsService.createOrderDetails(PlatFormMoneyTypeEnum.ORDER_REFUND,orderNo,orderMoney.negate());

    }

    /**
     * 陪玩师是否已经在服务用户
     * @return
     */
    @Override
    public Boolean isAlreadyService(Integer serverId){
        return redisOpenService.hasKey(RedisKeyEnum.USER_ORDER_ALREADY_SERVICE_KEY.generateKey(serverId));
    }

    /**
     * 删除陪玩师已经服务用户状态
     * @param serverId
     */
    private void  deleteAlreadyService(Integer serverId){
        redisOpenService.delete(RedisKeyEnum.USER_ORDER_ALREADY_SERVICE_KEY.generateKey(serverId));
    }


    public List<Order> findByStatusList(Integer[] statusList){
        OrderVO orderVO = new OrderVO();
        orderVO.setStatusList(statusList);
        return orderDao.findByParameter(orderVO);
    }



    /**
     * 生成订单号
     * @return
     */
    private String generateOrderNo(){
        String orderNo = GenIdUtil.GetOrderNo();
        if(findByOrderNo(orderNo)==null){
            return orderNo;
        }
        else{
            return generateOrderNo();
        }
    }


    public Order findByOrderNo(String orderNo){
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderNo(orderNo);
        List<Order> orderList =  orderDao.findByParameter(orderVO);
        if(orderList.isEmpty()){
            return null;
        }
        return orderList.get(0);
    }


    @Override
    public Boolean isOldUser(Integer userId){
        OrderVO orderVO = new OrderVO();
        orderVO.setUserId(userId);
        return orderDao.countByParameter(orderVO)>0;
    }


    private OrderVO orderConvertVo(Order order){
        OrderVO orderVO = new OrderVO();
        BeanUtil.copyProperties(order,orderVO);
        return orderVO;
    }
}
