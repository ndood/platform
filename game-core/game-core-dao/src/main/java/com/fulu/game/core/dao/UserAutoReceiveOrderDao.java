package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserAutoReceiveOrder;
import com.fulu.game.core.entity.vo.UserAutoReceiveOrderVO;
import com.fulu.game.core.entity.vo.searchVO.UserAutoOrderSearchVO;
import com.fulu.game.core.entity.vo.searchVO.UserInfoAuthSearchVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author wangbin
 * @email ${email}
 * @date 2018-07-26 19:42:13
 */
@Mapper
public interface UserAutoReceiveOrderDao extends ICommonDao<UserAutoReceiveOrder, Integer> {

    List<UserAutoReceiveOrder> findByParameter(UserAutoReceiveOrderVO userAutoReceiveOrderVO);


    List<Integer> findUserBySearch(UserAutoOrderSearchVO userAutoOrderSearchVO);

    List<UserAutoReceiveOrderVO> findAutoReceiveUserInfoAuthList(UserInfoAuthSearchVO userInfoAuthSearchVO);
}
