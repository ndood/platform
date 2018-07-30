package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserAutoReceiveOrder;
import com.fulu.game.core.entity.vo.UserAutoReceiveOrderVO;
import com.fulu.game.core.entity.vo.searchVO.UserAutoOrderSearchVO;
import com.fulu.game.core.entity.vo.searchVO.UserInfoAuthSearchVO;
import com.github.pagehelper.PageInfo;

import java.util.List;


/**
 * @author wangbin
 * @email ${email}
 * @date 2018-07-26 19:42:13
 */
public interface UserAutoReceiveOrderService extends ICommonService<UserAutoReceiveOrder, Integer> {

    /**
     * 给用户添加自动接单权限
     *
     * @param techAuthId
     * @param remark
     * @return
     */
    UserAutoReceiveOrder addAutoReceivingTech(Integer techAuthId, String remark);


    List<Integer> findUserBySearch(UserAutoOrderSearchVO userAutoOrderSearchVO);


    List<UserAutoReceiveOrder> findByUserId(int userId);


    UserAutoReceiveOrder findByUserIdAndCategoryId(int userId, int categoryId);


    void addOrderNum(int userId,int categoryId);



    void activateAutoOrder(int userId, boolean flag);

    /**
     * 添加完成订单数
     *
     * @param userId
     * @param categoryId
     */
    void addOrderCompleteNum(int userId, int categoryId);

    /**
     * 添加取消订单数
     *
     * @param userId
     * @param categoryId
     */
    void addOrderCancelNum(int userId, int categoryId);

    /**
     * 添加仲裁订单数
     *
     * @param userId
     * @param categoryId
     */
    void addOrderDisputeNum(int userId, int categoryId);

    /**
     * 自动接单陪玩师列表
     *
     * @param pageNum              页码
     * @param pageSize             每页显示数据条数
     * @param userInfoAuthSearchVO 查询VO
     * @return 自动接单陪玩师VO分页列表
     */
    PageInfo<UserAutoReceiveOrderVO> autoReceiveUserInfoAuthList(Integer pageNum,
                                                                 Integer pageSize,
                                                                 UserInfoAuthSearchVO userInfoAuthSearchVO);

    List<String> findAllAutoOrderUserHead();


}
