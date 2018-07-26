package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserAutoReceiveOrder;
import com.fulu.game.core.entity.vo.searchVO.UserAutoOrderSearchVO;

import java.util.List;


/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-26 19:42:13
 */
public interface UserAutoReceiveOrderService extends ICommonService<UserAutoReceiveOrder,Integer>{


    public UserAutoReceiveOrder addAutoReceivingTech(Integer techAuthId, String remark);


    List<Integer> findUserBySearch(UserAutoOrderSearchVO userAutoOrderSearchVO);
}
