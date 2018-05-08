package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderDealFile;
import com.fulu.game.core.entity.vo.OrderDealVO;
import com.fulu.game.core.service.OrderDealFileService;
import com.fulu.game.core.service.OrderService;
import com.xiaoleilu.hutool.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.OrderDealDao;
import com.fulu.game.core.entity.OrderDeal;
import com.fulu.game.core.service.OrderDealService;

import java.util.Date;
import java.util.List;


@Service
public class OrderDealServiceImpl extends AbsCommonService<OrderDeal,Integer> implements OrderDealService {

    @Autowired
	private OrderDealDao orderDealDao;
    @Autowired
    private OrderDealFileService orderDealFileService;
    @Autowired
    private OrderService orderService;

    @Override
    public ICommonDao<OrderDeal, Integer> getDao() {
        return orderDealDao;
    }

    @Override
    public void create(String orderNo, Integer userId,Integer type, String remark, String... fileUrls) {
        OrderDeal orderDeal = new OrderDeal();
        orderDeal.setOrderNo(orderNo);
        orderDeal.setType(type);
        orderDeal.setRemark(remark);
        orderDeal.setUserId(userId);
        orderDeal.setCreateTime(new Date());
        create(orderDeal);
        for(String url : fileUrls){
            OrderDealFile orderDealFile = new OrderDealFile();
            orderDealFile.setFileUrl(url);
            orderDealFile.setOrderDealId(orderDeal.getId());
            orderDealFile.setCreateTime(new Date());
            orderDealFileService.create(orderDealFile);
        }
    }

    @Override
    public OrderDealVO findByUserAndOrderNo(Integer userId, String orderNo){
        OrderDealVO param = new OrderDealVO();
        param.setUserId(userId);
        param.setOrderNo(orderNo);
        List<OrderDeal> orderDealList = orderDealDao.findByParameter(param);
        if(orderDealList.isEmpty()){
            return null;
        }
        OrderDealVO orderDealVO = new OrderDealVO();
        BeanUtil.copyProperties(orderDealList.get(0),orderDealVO);
        List<OrderDealFile> orderDealFiles =  orderDealFileService.findByOrderDeal(orderDealVO.getId());
        orderDealVO.setOrderDealFileList(orderDealFiles);
        return orderDealVO;
    }

    /**
     * 查看陪玩师提交的验收结果
     * @param orderNo
     * @return
     */
    @Override
    public OrderDealVO findOrderAcceptanceResult(String orderNo){
        Order order = orderService.findByOrderNo(orderNo);
        return findByUserAndOrderNo(order.getServiceUserId(),orderNo);
    }
}
