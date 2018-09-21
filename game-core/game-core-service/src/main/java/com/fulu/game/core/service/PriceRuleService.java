package com.fulu.game.core.service;

import com.fulu.game.core.entity.PriceRule;
import com.fulu.game.core.entity.vo.PriceRuleVO;

import java.util.List;


/**
 * 定价规则表
 * 
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-12 15:43:00
 */
public interface PriceRuleService extends ICommonService<PriceRule,Integer>{

    /**
     * 通过分类id获取定价规则列表
     * @param id
     * @return
     */
    List<PriceRule> findByCategoryId(Integer id);

    List<PriceRuleVO> findUserPriceByCategoryId(Integer id,int userId);
}
