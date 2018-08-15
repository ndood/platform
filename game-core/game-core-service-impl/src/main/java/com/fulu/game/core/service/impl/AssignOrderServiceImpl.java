package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.TechAuthStatusEnum;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.searchVO.UserAutoOrderSearchVO;
import com.fulu.game.core.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
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
        userIds.removeIf(userId -> (isDisabledUser(userId,order)));
        log.info("查询出可以自动派单的用户有:{}",userIds);
        return userService.findByUserIds(userIds, Boolean.TRUE);
    }

    /**
     * 判断这个用户ID是否可用
     * @param userId
     * @return
     */
    private Boolean isDisabledUser(Integer userId,Order order) {
        if(userId.equals(order.getUserId())){
            log.info("userId:{},和下单用户一致不能派单",userId);
            return true;
        }
        if (redisOpenService.hasKey(RedisKeyEnum.AUTO_ASSIGN_ORDER_USER.generateKey(userId))){
            log.info("userId:{},有30分钟派单限制不能派单",userId);
            return true;
        }
        List<UserTechAuth> userTechAuths = userTechAuthService.findByCategoryAndUser(order.getCategoryId(),userId);
        if(userTechAuths.isEmpty()){
            log.info("userId:{},用户技能为空不能派单",userId);
            return true;
        }
        UserTechAuth userTechAuth = userTechAuths.get(0);
        if(!TechAuthStatusEnum.NORMAL.getType().equals(userTechAuth.getStatus())){
            log.info("userId:{},用户技能没有认证通过不能派单",userId);
            return true;
        }
       return false;
    }


}
