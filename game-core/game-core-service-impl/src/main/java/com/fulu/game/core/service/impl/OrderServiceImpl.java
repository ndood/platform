package com.fulu.game.core.service.impl;


import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.OrderProduct;
import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.OrderProductService;
import com.fulu.game.core.service.ProductService;
import com.xiaoleilu.hutool.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.OrderDao;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.service.OrderService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Service
public class OrderServiceImpl extends AbsCommonService<Order,Integer> implements OrderService {

    @Autowired
	private OrderDao orderDao;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OrderProductService orderProductService;

    @Override
    public ICommonDao<Order, Integer> getDao() {
        return orderDao;
    }

    @Override
    public OrderVO submit(int productId,
                          int num,
                          String remark) {

        //todo 确认打手是否已经接单
        Product product = productService.findById(productId);

        Category category = categoryService.findById(productId);
        //计算订单总价格
        BigDecimal totalMoney = product.getPrice().multiply(new BigDecimal(num));
        //计算单笔订单佣金
        BigDecimal commissionMoney = totalMoney.multiply(category.getCharges());
        //创建订单
        Order order = new Order();
        order.setName(product.getCategoryName()+"-"+num+"*"+product.getUnit());
        order.setOrderNo(getOrderNo());
        order.setUserId(Constant.DEF_USER_ID);
        order.setServiceUserId(product.getUserId());
        order.setRemark(remark);
        order.setIsPlay(false);
        order.setTotalMoney(totalMoney);
        order.setStatus(OrderStatusEnum.NON_PAYMENT.getStatus());
        order.setCommissionMoney(commissionMoney);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        create(order);
        //创建订单商品
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderNo(order.getOrderNo());
        orderProduct.setAmount(num);
        orderProduct.setPrice(product.getPrice());
        orderProduct.setProductId(product.getId());
        orderProduct.setProductName(order.getName());
        orderProduct.setCreateTime(new Date());
        orderProduct.setUpdateTime(new Date());
        orderProductService.create(orderProduct);
        OrderVO orderVO = new OrderVO();
        BeanUtil.copyProperties(order,orderVO);
        return orderVO;
    }




    /**
     * 生成订单号
     * @return
     */
    private String getOrderNo(){
        String orderNo = GenIdUtil.GetOrderNo();
        if(findByOrderNo(orderNo)==null){
            return orderNo;
        }
        else{
            return getOrderNo();
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
}
