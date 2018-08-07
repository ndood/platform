package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.core.entity.GradingPrice;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderPointProduct;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.searchVO.UserAutoOrderSearchVO;
import com.fulu.game.core.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssignOrderServiceImpl implements AssignOrderService {

    @Autowired
    private UserService userService;
    @Autowired
    private GradingPriceService gradingPriceService;
    @Autowired
    private OrderPointProductService orderPointProductService;
    @Autowired
    private UserAutoReceiveOrderService userAutoReceiveOrderService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;


    public User getMatchUser(Order order){
        List<User> users = findMatchUsers(order);
        if(users.isEmpty()){
            return null;
        }
        return users.get(0);
    }


    /**
     * 查找到匹配的用户
     *
     * @param order
     * @return
     */
    private List<User> findMatchUsers(Order order) {
        OrderPointProduct orderPointProduct = orderPointProductService.findByOrderNo(order.getOrderNo());
        Integer areaId = orderPointProduct.getAreaId();
        Integer gradingPriceId = orderPointProduct.getGradingPriceId();
        Integer targetGradingPriceId = orderPointProduct.getTargetGradingPriceId();

        GradingPrice gradingPrice = gradingPriceService.findById(gradingPriceId);
        GradingPrice targetGradingPrice = gradingPriceService.findById(targetGradingPriceId);

        Integer startRank = gradingPrice.getRank();
        Integer endRank = targetGradingPrice.getRank();

        UserAutoOrderSearchVO userAutoOrderSearchVO = new UserAutoOrderSearchVO();
        userAutoOrderSearchVO.setCategoryId(order.getCategoryId());
        userAutoOrderSearchVO.setAreaId(areaId);
        userAutoOrderSearchVO.setStartRank(startRank);
        userAutoOrderSearchVO.setEndRank(endRank);
        userAutoOrderSearchVO.setUserAutoSetting(Boolean.TRUE);
        List<Integer> userIds = userAutoReceiveOrderService.findUserBySearch(userAutoOrderSearchVO);
        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }

        Integer orderUserId = order.getUserId();
        userIds.removeIf(userId -> (userId.equals(orderUserId)));
        userIds.removeIf(userId -> redisOpenService.hasKey(RedisKeyEnum.AUTO_ASSIGN_ORDER_USER.generateKey(userId)));
        return userService.findByUserIds(userIds,Boolean.TRUE);
    }

}
