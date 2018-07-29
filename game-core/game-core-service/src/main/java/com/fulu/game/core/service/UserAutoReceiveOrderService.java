package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserAutoReceiveOrder;
import com.fulu.game.core.entity.vo.searchVO.UserAutoOrderSearchVO;

import java.util.List;


/**
 * @author wangbin
 * @email ${email}
 * @date 2018-07-26 19:42:13
 */
public interface UserAutoReceiveOrderService extends ICommonService<UserAutoReceiveOrder, Integer> {

    /**
     * 给用户添加自动接单权限
     * @param techAuthId
     * @param remark
     * @return
     */
    UserAutoReceiveOrder addAutoReceivingTech(Integer techAuthId, String remark);


    List<Integer> findUserBySearch(UserAutoOrderSearchVO userAutoOrderSearchVO);


    List<UserAutoReceiveOrder> findByUserId(int userId);


    UserAutoReceiveOrder findByUserIdAndCategoryId(int userId,int categoryId);


    void activateAutoOrder(int userId, boolean flag);

    /**
     * 添加完成订单数
     * @param userId
     * @param categoryId
     */
    void addOrderCompleteNum(int userId,int categoryId);

    /**
     * 添加取消订单数
     * @param userId
     * @param categoryId
     */
    void addOrderCancelNum(int userId,int categoryId);

    /**
     * 添加仲裁订单数
     * @param userId
     * @param categoryId
     */
    void addOrderDisputeNum(int userId, int categoryId);


}
