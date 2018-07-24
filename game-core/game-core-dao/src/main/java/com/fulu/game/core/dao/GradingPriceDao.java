package com.fulu.game.core.dao;

import com.fulu.game.core.entity.GradingPrice;
import com.fulu.game.core.entity.vo.GradingPriceVO;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 段位定级表
 * @author wangbin
 * @email ${email}
 * @date 2018-07-23 19:34:58
 */
@Mapper
public interface GradingPriceDao extends ICommonDao<GradingPrice,Integer>{

    List<GradingPrice> findByParameter(GradingPriceVO gradingPriceVO);


    BigDecimal findRangePrice(@Param(value = "categoryId") Integer categoryId,
                              @Param(value = "startRank")  Integer startRank,
                              @Param(value = "endRank") Integer endRank);



}
