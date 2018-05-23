package com.fulu.game.core.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.OrderProductDao;
import com.fulu.game.core.entity.OrderDealFile;
import com.fulu.game.core.entity.OrderProduct;
import com.fulu.game.core.entity.vo.OrderDealVO;
import com.fulu.game.core.entity.vo.OrderProductVO;
import com.fulu.game.core.entity.vo.requestVO.OrderReqVO;
import com.fulu.game.core.entity.vo.responseVO.OrderResVO;
import com.fulu.game.core.service.OrderDealService;
import com.fulu.game.core.service.OrderProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("orderProductService")
public class OrderProductServiceImpl extends AbsCommonService<OrderProduct, Integer> implements OrderProductService {

    @Autowired
    private OrderProductDao orderProductDao;
    @Autowired
    private OrderDealService orderDealService;

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
    public PageInfo<OrderResVO> list(OrderReqVO orderReqVO,Integer pageNum,Integer pageSize,String orderBy) {
        if(StringUtils.isBlank(orderBy)){
            orderBy = "id DESC";
        }
        PageHelper.startPage(pageNum,pageSize, orderBy);
        Integer status = orderReqVO.getStatus();
        Integer[] statusList = OrderStatusGroupEnum.getByValue(status);
        if (null != statusList && statusList.length > 0) {
            orderReqVO.setStatusList(statusList);
        }
        List<OrderResVO> list = orderProductDao.findByUnionParam(orderReqVO);
        if (!CollectionUtil.isEmpty(list)){
            for(OrderResVO orderResVO:list){
                OrderDealVO userOrderDealVO = orderDealService.findByUserAndOrderNo(orderResVO.getUserId(),orderResVO.getOrderNo());
                List<String> userOrderDealFileList = getOrderDealFileStrList(userOrderDealVO);
                orderResVO.setUserOrderDealFileList(userOrderDealFileList);
                OrderDealVO ServerOrderDealVO = orderDealService.findByUserAndOrderNo(orderResVO.getServerUserId(),orderResVO.getOrderNo());
                List<String> serverOrderDealFileList = getOrderDealFileStrList(ServerOrderDealVO);
                orderResVO.setServerOrderDealFileList(serverOrderDealFileList);
            }
        }
        return new PageInfo(list);
    }

    private List<String> getOrderDealFileStrList(OrderDealVO orderDealVO){
        if (null ==orderDealVO){
            return new ArrayList<>();
        }
        List<OrderDealFile> orderDealFileList = orderDealVO.getOrderDealFileList();
        if (CollectionUtil.isEmpty(orderDealFileList)){
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>();
        for (OrderDealFile orderDealFile:orderDealFileList) {
            list.add(orderDealFile.getFileUrl());
        }
        return list;
    }
}
