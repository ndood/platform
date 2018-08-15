package com.fulu.game.h5.service.impl;

import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.common.enums.UserTypeEnum;
import com.fulu.game.common.exception.ProductException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.dao.OrderDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.OrderDetailsVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.OrderServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class H5OrderServiceImpl extends OrderServiceImpl {
    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserContactService userContactService;
    private final OrderProductService orderProductService;
    private final H5CouponServiceImpl couponService;
    private final OrderStatusDetailsService orderStatusDetailsService;
    private final OrderDao orderDao;

    @Autowired
    public H5OrderServiceImpl(UserService userService,
                              ProductService productService,
                              CategoryService categoryService,
                              UserContactService userContactService,
                              OrderProductService orderProductService,
                              H5CouponServiceImpl couponService,
                              OrderStatusDetailsService orderStatusDetailsService,
                              OrderDao orderDao) {
        this.userService = userService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.userContactService = userContactService;
        this.orderProductService = orderProductService;
        this.couponService = couponService;
        this.orderStatusDetailsService = orderStatusDetailsService;
        this.orderDao = orderDao;
    }

    @Override
    protected void dealOrderAfterPay(Order order) {

    }

    @Override
    protected void shareProfit(Order order) {

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
        order.setType(OrderTypeEnum.H5.getType());
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
}
