package com.fulu.game.core.service.impl;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.OrderDao;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl extends AbsCommonService<Order, Integer> implements OrderService {

    @Autowired
    private OrderDao orderDao;


    @Override
    public ICommonDao<Order, Integer> getDao() {
        return orderDao;
    }

    @Override
    public int count(Integer serverId, Integer[] statusList, Date startTime, Date endTime) {
        OrderVO params = new OrderVO();
        params.setServiceUserId(serverId);
        params.setStatusList(statusList);
        params.setStartTime(startTime);
        params.setEndTime(endTime);
        return orderDao.countByParameter(params);
    }

    @Override
    public int weekOrderCount(Integer serverId) {
        Date startTime = DateUtil.beginOfWeek(new Date());
        Date endTime = DateUtil.endOfWeek(new Date());
        Integer[] statusList = OrderStatusGroupEnum.ALL_NORMAL_COMPLETE.getStatusList();
        return count(serverId, statusList, startTime, endTime);
    }

    @Override
    public int allOrderCount(Integer serverId) {
        Integer[] statusList = OrderStatusGroupEnum.ALL_NORMAL_COMPLETE.getStatusList();
        return count(serverId, statusList, null, null);
    }

    @Override
    public List<Order> findByStatusList(Integer[] statusList) {
        if (statusList == null) {
            return new ArrayList<>();
        }
        OrderVO param = new OrderVO();
        param.setStatusList(statusList);
        return orderDao.findByParameter(param);
    }



    @Override
    public Order findByOrderNo(String orderNo) {
        if (orderNo == null) {
            return null;
        }
        return orderDao.findByOrderNo(orderNo);
    }

    @Override
    public Boolean isOldUser(Integer userId) {
        OrderVO orderVO = new OrderVO();
        orderVO.setUserId(userId);
        return orderDao.countByParameter(orderVO) > 0;
    }

    @Override
    public List<Order> findWaitSendEmailOrder(Integer status , Integer waitMins) {

        OrderVO params = new OrderVO();
        params.setStatus(status);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(new Date());
        rightNow.add(Calendar.MINUTE,waitMins.intValue()*-1);
        params.setEndTime(rightNow.getTime());
        
        return orderDao.findByParameter(params);
    }
    
}
