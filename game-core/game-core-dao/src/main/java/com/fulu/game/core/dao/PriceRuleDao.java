package com.fulu.game.core.dao;

import com.fulu.game.core.entity.PriceRule;
import com.fulu.game.core.entity.vo.PriceRuleVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 定价规则表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-12 15:43:00
 */
@Mapper
public interface PriceRuleDao extends ICommonDao<PriceRule,Integer>{

    List<PriceRule> findByParameter(PriceRuleVO priceRuleVO);

}
