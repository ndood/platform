package com.fulu.game.core.service.impl;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.common.enums.OrderStatusGroupEnum;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.OrderDao;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.OrderDetailsVO;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl extends AbsCommonService<Order, Integer> implements OrderService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private UserService userService;


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
    public List<Order> findWaitSendEmailOrder(Integer status, Integer waitMins) {

        OrderVO params = new OrderVO();
        params.setStatus(status);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(new Date());
        rightNow.add(Calendar.MINUTE, waitMins.intValue() * -1);
        params.setEndTime(rightNow.getTime());

        return orderDao.findByParameter(params);
    }

    @Override
    public List<OrderDetailsVO> orderList(int type, int userId, List<Integer> statusList) {
        List<OrderDetailsVO> list = orderDao.listOrderDetails(type, userId, statusList);
        return list;
    }

    @Override
    public OrderVO getThunderOrderInfo() {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }


        OrderVO paramOrderVO = new OrderVO();
        paramOrderVO.setUserId(user.getId());
        paramOrderVO.setPlatform(PlatformEcoEnum.THUNDER.getType());
        paramOrderVO.setStatusList(OrderStatusGroupEnum.ADMIN_COMPLETE.getStatusList());
        int orderCount = orderDao.countByParameter(paramOrderVO);

        OrderVO orderVO = orderDao.findMoneySum(paramOrderVO);
        orderVO.setOrderCount(orderCount);
        BigDecimal userConsumeMoney = orderVO.getSumActualMoney().subtract(orderVO.getSumUserMoney()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        if (userConsumeMoney.compareTo(BigDecimal.ZERO) < 0) {
            log.info("迅雷订单数据异常，累计实付金额：{}，累计退款用户金额：{}", orderVO.getSumActualMoney(), orderVO.getSumUserMoney());
            userConsumeMoney = BigDecimal.ZERO;
        }

        orderVO.setUserConsumeMoney(userConsumeMoney);
        orderVO.setOrderCount(orderCount);
        return orderVO;
    }


    @Override
    public List<Order> getBannerOrderList(Integer authUserId, Integer bossUserId) {

        List<Integer> list = new ArrayList<Integer>();
        list.add(OrderStatusEnum.WAIT_SERVICE.getStatus());
        list.add(OrderStatusEnum.ALREADY_RECEIVING.getStatus());
        list.add(OrderStatusEnum.SERVICING.getStatus());
        list.add(OrderStatusEnum.CHECK.getStatus());
        list.add(OrderStatusEnum.CONSULTING.getStatus());
        list.add(OrderStatusEnum.CONSULT_REJECT.getStatus());
        list.add(OrderStatusEnum.CONSULT_CANCEL.getStatus());
        list.add(OrderStatusEnum.APPEALING.getStatus());

        List<Order> orderList = orderDao.getBannerOrderList(authUserId, bossUserId, list);

        return orderList;
    }
}
