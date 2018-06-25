package com.fulu.game.core.dao;

import com.fulu.game.core.entity.PriceFactor;
import com.fulu.game.core.entity.vo.PriceFactorVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 价格系数表
 * @author wangbin
 * @email ${email}
 * @date 2018-06-25 18:07:42
 */
@Mapper
public interface PriceFactorDao extends ICommonDao<PriceFactor,Integer>{

    List<PriceFactor> findByParameter(PriceFactorVO priceFactorVO);

}
