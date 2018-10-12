package com.fulu.game.h5.service.impl;

import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.common.enums.PaymentEnum;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.exception.ProductException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 迅雷订单服务实现类
 *
 * @author Gong ZeChun
 * @date 2018/10/12 12:44
 */
@Service
@Slf4j
public class ThunderOrderServiceImpl extends H5OrderServiceImpl {
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
    private UserContactService userContactService;
    @Autowired
    private OrderProductService orderProductService;
    @Autowired
    private OrderStatusDetailsService orderStatusDetailsService;

    @Override
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
        order.setPlatform(PlatformEcoEnum.THUNDER.getType());
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
}
