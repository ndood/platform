package com.fulu.game.core.service.impl;

import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.OrderProductDao;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderProduct;
import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.vo.OrderDealVO;
import com.fulu.game.core.entity.vo.OrderProductVO;
import com.fulu.game.core.entity.vo.responseVO.OrderResVO;
import com.fulu.game.core.entity.vo.searchVO.OrderSearchVO;
import com.fulu.game.core.service.OrderDealService;
import com.fulu.game.core.service.OrderProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("orderProductService")
public class OrderProductServiceImpl extends AbsCommonService<OrderProduct, Integer> implements OrderProductService {

    @Autowired
    private OrderProductDao orderProductDao;


    @Override
    public ICommonDao<OrderProduct, Integer> getDao() {
        return orderProductDao;
    }

    @Override
    public OrderProduct findByOrderNo(String orderNo) {
        OrderProductVO orderProductVO = new OrderProductVO();
        orderProductVO.setOrderNo(orderNo);
        List<OrderProduct> orderProductList = orderProductDao.findByParameter(orderProductVO);
        if (orderProductList.isEmpty()) {
            return null;
        }
        return orderProductList.get(0);
    }

    @Override
    public OrderProduct create(Order order, Product product, int num) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderNo(order.getOrderNo());
        orderProduct.setAmount(num);
        orderProduct.setUnit(product.getUnit());
        orderProduct.setPrice(product.getPrice());
        orderProduct.setProductId(product.getId());
        orderProduct.setProductName(order.getName());
        orderProduct.setCreateTime(new Date());
        orderProduct.setUpdateTime(new Date());
        create(orderProduct);
        return orderProduct;
    }


}
