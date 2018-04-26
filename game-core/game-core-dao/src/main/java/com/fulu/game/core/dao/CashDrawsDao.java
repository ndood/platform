package com.fulu.game.core.dao;

import com.fulu.game.core.entity.CashDraws;
import com.fulu.game.core.entity.vo.CashDrawsVO;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author yanbiao
 * @date 2018-04-24 16:45:40
 */
@Mapper
public interface CashDrawsDao extends ICommonDao<CashDraws,Integer>{

    List<CashDraws> findByParameter(CashDrawsVO cashDrawsVO);

}
