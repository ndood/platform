package com.fulu.game.core.dao;

import com.fulu.game.core.entity.ConversionRate;
import com.fulu.game.core.entity.vo.ConversionRateVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 转换率统计表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-25 11:52:10
 */
@Mapper
public interface ConversionRateDao extends ICommonDao<ConversionRate,Integer>{

    List<ConversionRate> findByParameter(ConversionRateVO conversionRateVO);

}
