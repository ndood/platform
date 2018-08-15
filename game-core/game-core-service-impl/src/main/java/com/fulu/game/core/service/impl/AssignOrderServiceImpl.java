package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.TechAuthStatusEnum;
import com.fulu.game.core.entity.*;
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
    @Autowired
    private UserTechAuthService userTechAuthService;


    public User getMatchUser(Order order) {
        List<User> users = findMatchUsers(order);
        if (users.isEmpty()) {
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
        userIds.removeIf(userId -> (userId.equals(orderUserId) || isDisabledUser(userId,order.getCategoryId())));
        return userService.findByUserIds(userIds, Boolean.TRUE);
    }

    /**
     * 判断这个用户ID是否可用
     * @param userId
     * @return
     */
    private Boolean isDisabledUser(Integer userId,Integer categoryId) {
        if (redisOpenService.hasKey(RedisKeyEnum.AUTO_ASSIGN_ORDER_USER.generateKey(userId))){
            return true;
        }
        List<UserTechAuth> userTechAuths = userTechAuthService.findByCategoryAndUser(userId,categoryId);
        if(userTechAuths.isEmpty()){
            return true;
        }
        UserTechAuth userTechAuth = userTechAuths.get(0);
        return !TechAuthStatusEnum.NORMAL.getType().equals(userTechAuth.getStatus());
    }


}
