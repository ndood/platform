package com.fulu.game.core.dao;

import com.fulu.game.core.entity.MoneyDetails;
import com.fulu.game.core.entity.vo.MoneyDetailsVO;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 零钱流水表
 * @author yanbiao
 * @date 2018-04-25 14:59:54
 */
@Mapper
public interface MoneyDetailsDao extends ICommonDao<MoneyDetails,Integer>{

    List<MoneyDetails> findByParameter(MoneyDetailsVO moneyDetailsVO);

    List<MoneyDetailsVO> findByAdmin(MoneyDetailsVO moneyDetailsVO);

    List<MoneyDetailsVO> findByUser(MoneyDetailsVO moneyDetailsVO);


    List<MoneyDetails> findUserMoneyByAction(@Param(value = "targetId") Integer targetId,@Param(value = "actionList") List<Integer> actionList,@Param(value = "startTime") Date startTime,@Param(value = "endTime") Date endTime);

}
