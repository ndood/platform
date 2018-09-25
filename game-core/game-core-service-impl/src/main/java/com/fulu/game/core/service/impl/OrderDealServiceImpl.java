package com.fulu.game.core.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.utils.CollectionUtil;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.OrderDealDao;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderDeal;
import com.fulu.game.core.entity.OrderDealFile;
import com.fulu.game.core.entity.vo.OrderDealVO;
import com.fulu.game.core.service.OrderDealFileService;
import com.fulu.game.core.service.OrderDealService;
import com.fulu.game.core.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class OrderDealServiceImpl extends AbsCommonService<OrderDeal, Integer> implements OrderDealService {

    @Autowired
    private OrderDealDao orderDealDao;
    @Autowired
    private OrderDealFileService orderDealFileService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OssUtil ossUtil;

    @Override
    public ICommonDao<OrderDeal, Integer> getDao() {
        return orderDealDao;
    }


    @Override
    public int create(OrderDeal orderDeal) {
        orderDeal.setCreateTime(new Date());
        return orderDealDao.create(orderDeal);
    }


    @Override
    public void create(OrderDeal orderDeal, String... fileUrls) {
        
        orderDeal.setCreateTime(new Date());
        create(orderDeal);
        
        if (fileUrls != null) {
            for (String url : fileUrls) {
                OrderDealFile orderDealFile = new OrderDealFile();
                orderDealFile.setFileUrl(ossUtil.activateOssFile(url));
                orderDealFile.setOrderDealId(orderDeal.getId());
                orderDealFile.setCreateTime(new Date());
                orderDealFileService.create(orderDealFile);
            }
        }
    }

    @Override
    public OrderDealVO findByUserAndOrderNo(Integer userId, String orderNo) {
        if (userId == null) {
            return null;
        }
        OrderDealVO param = new OrderDealVO();
        param.setUserId(userId);
        param.setOrderNo(orderNo);
        List<OrderDeal> orderDealList = orderDealDao.findByParameter(param);
        if (orderDealList.isEmpty()) {
            return null;
        }
        OrderDealVO orderDealVO = new OrderDealVO();
        BeanUtil.copyProperties(orderDealList.get(0), orderDealVO);
        List<OrderDealFile> orderDealFiles = orderDealFileService.findByOrderDeal(orderDealVO.getId());
        orderDealVO.setOrderDealFileList(orderDealFiles);
        return orderDealVO;
    }

    /**
     * 查看陪玩师提交的验收结果
     *
     * @param orderNo
     * @return
     */
    @Override
    public OrderDealVO findOrderAcceptanceResult(String orderNo) {
        Order order = orderService.findByOrderNo(orderNo);
        return findByUserAndOrderNo(order.getServiceUserId(), orderNo);
    }

    @Override
    public List<OrderDealVO> findByOrderEventId(Integer orderEventId) {
        if (orderEventId == null) {
            return new ArrayList<>();
        }
        OrderDealVO param = new OrderDealVO();
        param.setOrderEventId(orderEventId);
        List<OrderDeal> orderDealList = orderDealDao.findByParameter(param);
        if (orderDealList.isEmpty()) {
            return new ArrayList<>();
        }
        List<OrderDealVO> orderDealVOList = CollectionUtil.copyNewCollections(orderDealList, OrderDealVO.class);
        for (OrderDealVO orderDealVO : orderDealVOList) {
            List<OrderDealFile> orderDealFiles = orderDealFileService.findByOrderDeal(orderDealVO.getId());
            orderDealVO.setOrderDealFileList(orderDealFiles);
        }
        return orderDealVOList;
    }
}
