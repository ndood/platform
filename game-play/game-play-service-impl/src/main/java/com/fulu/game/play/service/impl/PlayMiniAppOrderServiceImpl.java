package com.fulu.game.play.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.common.exception.ProductException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.core.dao.OrderDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.OrderDetailsVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.aop.UserScore;
import com.fulu.game.core.service.impl.AbOrderOpenServiceImpl;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.core.service.impl.push.MiniAppPushServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class PlayMiniAppOrderServiceImpl extends AbOrderOpenServiceImpl {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OrderProductService orderProductService;
    @Autowired
    private UserService userService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private OrderStatusDetailsService orderStatusDetailsService;
    @Autowired
    private UserCommentService userCommentService;
    @Autowired
    private UserContactService userContactService;
    @Autowired
    private PlayOrderShareProfitServiceImpl playOrderShareProfitService;
    @Autowired
    private PlayMiniAppPushServiceImpl playMiniAppPushService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Qualifier(value = "userInfoAuthServiceImpl")
    @Autowired
    private UserInfoAuthService userInfoAuthService;
    @Autowired
    private ImService imService;

    /**
     * 陪玩师接单
     *
     * @return
     */
    @UserScore(type = UserScoreEnum.ACCEPT_ORDER)
    public String serverReceiveOrder(Order order) {
        log.info("执行开始接单接口");
        log.info("陪玩师接单orderNo:{}", order.getOrderNo());

        //只有等待陪玩和已支付的订单才能开始陪玩
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_SERVICE.getStatus()) || !order.getIsPay()) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_STATUS_MISMATCHES, order.getOrderNo());
        }
        order.setStatus(OrderStatusEnum.ALREADY_RECEIVING.getStatus());
        order.setUpdateTime(new Date());
        order.setReceivingTime(new Date());
        orderService.update(order);
        //计算订单状态倒计时24小时
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 24 * 60);

        playMiniAppPushService.receiveOrder(order);

        return order.getOrderNo();
    }

    public PageInfo<OrderDetailsVO> list(int pageNum, int pageSize, Integer type) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        User user = userService.getCurrentUser();
        List<OrderDetailsVO> list = orderDao.listOrderDetails(type, user.getId(),null);

        //获取未读订单
        String wronJsonStr = redisOpenService.get(RedisKeyEnum.USER_WAITING_READ_ORDER.generateKey(user.getId()));

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

            //设置订单未读状态

            if (StringUtils.isNotBlank(wronJsonStr)) {
                JSONArray waitingReadOrderNo = JSONObject.parseArray(wronJsonStr);

                for (int i = 0; i < waitingReadOrderNo.size(); i++) {
                    if (waitingReadOrderNo.getString(i).equals(orderDetailsVO.getOrderNo())) {
                        orderDetailsVO.setWaitingRead(true);
                        break;
                    }
                }
            }
        }
        return new PageInfo<>(list);
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

        //删除订单未读状态

        String wronJsonStr = redisOpenService.get(RedisKeyEnum.USER_WAITING_READ_ORDER.generateKey(currentUser.getId()));
        if (StringUtils.isNotBlank(wronJsonStr)) {
            JSONArray waitingReadOrderNo = JSONObject.parseArray(wronJsonStr);

            for (int i = 0; i < waitingReadOrderNo.size(); i++) {
                if (waitingReadOrderNo.getString(i).equals(order.getOrderNo())) {
                    waitingReadOrderNo.remove(i);
                    redisOpenService.set(RedisKeyEnum.USER_WAITING_READ_ORDER.generateKey(order.getServiceUserId()), waitingReadOrderNo.toJSONString());
                    break;
                }
            }
        }

        return orderDetailsVO;
    }

    /**
     * 提交陪玩订单
     *
     * @param productId
     * @param num
     * @param remark
     * @param couponNo
     * @param userIp
     * @param contactType
     * @param contactInfo
     * @return
     */
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
        order.setType(OrderTypeEnum.PLAY.getType());
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
                throw new OrderException(OrderException.ExceptionCode.ORDER_COUPON_NOT_USE);
            }
        }
        if (order.getUserId().equals(order.getServiceUserId())) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_NOT_MYSELF);
        }
        //创建订单
        orderService.create(order);

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
            agentIm = userInfoAuth.getOpenSubstituteIm()==null?false:userInfoAuth.getOpenSubstituteIm();
        }

        if (!vestFlag) {
            SMSUtil.sendOrderReceivingRemind(server.getMobile(), order.getName());
            //推送通知
            playMiniAppPushService.orderPay(order);
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
        
        
        //判断陪玩师是否为马甲或者代聊陪玩师  如果是，则给陪玩师发送一条im消息告诉他被下单了
        if (vestFlag||agentIm) {

            User user = userService.findById(order.getUserId());

            Map<String, String> extMap = new HashMap<>();
            extMap.put("msg","系统提示：用户下了新订单");
            
            imService.sendMsgToImUser(new String[]{server.getImId()}, user.getImId() , Constant.SERVICE_USER_PAY_ORDER, extMap);
        }

    }

    @Override
    protected void shareProfit(Order order) {
        playOrderShareProfitService.shareProfit(order);
    }

    @Override
    protected MiniAppPushServiceImpl getMinAppPushService() {
        return playMiniAppPushService;
    }

    @Override
    public void orderRefund(Order order, BigDecimal refundMoney) {
        playOrderShareProfitService.orderRefund(order, refundMoney);
    }

}
