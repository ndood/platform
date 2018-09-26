package com.fulu.game.h5.service.impl.fenqile;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.common.enums.UserTypeEnum;
import com.fulu.game.common.exception.ProductException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.core.dao.OrderDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.OrderDetailsVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.AbOrderOpenServiceImpl;
import com.fulu.game.core.service.impl.push.MiniAppPushServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    private H5PushServiceImpl h5PushService;
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
    private FenqileOrderService fenqileOrderService;


    @Override
    protected MiniAppPushServiceImpl getMinAppPushService() {
        return h5PushService;
    }

    @Override
    protected void dealOrderAfterPay(Order order) {
        //订单状态倒计时
        orderStatusDetailsService.create(order.getOrderNo(), order.getStatus(), 24 * 60);
        //发送短信通知给陪玩师
        User server = userService.findById(order.getServiceUserId());

        UserInfoAuth userInfoAuth = userInfoAuthService.findByUserId(order.getServiceUserId());
        Boolean vestFlag = false;
        if (userInfoAuth != null) {
            vestFlag = userInfoAuth.getVestFlag() == null ? false : userInfoAuth.getVestFlag();
        }

        if (!vestFlag) {
            SMSUtil.sendOrderReceivingRemind(server.getMobile(), order.getName());
            //推送通知
            h5PushService.orderPay(order);
        }
    }

    @Override
    protected void shareProfit(Order order) {
        h5OrderShareProfitService.shareProfit(order);
    }

    @Override
    protected void orderRefund(Order order, BigDecimal refundMoney) {

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
        order.setType(OrderTypeEnum.PLATFORM.getType());
        order.setOrderNo(generateOrderNo());
        order.setUserId(user.getId());
        order.setServiceUserId(product.getUserId());
        order.setCategoryId(product.getCategoryId());
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
        orderService.create(order);

        //保存联系方式
        userContactService.save(user.getId(), order.getContactType(), order.getContactInfo());

        //更新优惠券使用状态
        if (coupon != null) {
            couponService.updateCouponUseStatus(order.getOrderNo(), userIp, coupon);
        }
        //创建订单商品
        orderProductService.create(order, product, num);

        //新建分期乐订单数据
        FenqileOrder fenqileOrder = new FenqileOrder();
        fenqileOrder.setOrderNo(order.getOrderNo());
        fenqileOrder.setUpdateTime(DateUtil.date());
        fenqileOrder.setCreateTime(DateUtil.date());
        fenqileOrderService.create(fenqileOrder);

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


}
