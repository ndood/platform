package com.fulu.game.h5.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.enums.OrderTypeEnum;
import com.fulu.game.common.exception.ProductException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.CouponServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

import static com.fulu.game.common.enums.OrderStatusEnum.NON_PAYMENT;

@Service
@Slf4j
public class PilotMiniAppOrderServiceImpl extends PlayMiniAppOrderServiceImpl {

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
    private PriceFactorService priceFactorService;
    @Autowired
    private PilotOrderService pilotOrderService;
    @Autowired
    private OrderStatusDetailsService orderStatusDetailsService;
    @Autowired
    private UserContactService userContactService;
    @Autowired
    private OrderService orderService;

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
        orderService.create(order);
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

}
