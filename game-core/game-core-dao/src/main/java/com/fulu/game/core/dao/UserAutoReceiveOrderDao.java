package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserAutoReceiveOrder;
import com.fulu.game.core.entity.vo.UserAutoReceiveOrderVO;

import java.util.List;

import com.fulu.game.core.entity.vo.searchVO.UserAutoOrderSearchVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-26 19:42:13
 */
@Mapper
public interface UserAutoReceiveOrderDao extends ICommonDao<UserAutoReceiveOrder,Integer>{

    List<UserAutoReceiveOrder> findByParameter(UserAutoReceiveOrderVO userAutoReceiveOrderVO);


    List<Integer> findUserBySearch(UserAutoOrderSearchVO userAutoOrderSearchVO);
}
