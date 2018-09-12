package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.PriceRuleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.PriceRuleDao;
import com.fulu.game.core.entity.PriceRule;
import com.fulu.game.core.service.PriceRuleService;

import java.util.List;


@Service
public class PriceRuleServiceImpl extends AbsCommonService<PriceRule,Integer> implements PriceRuleService {

    @Autowired
	private PriceRuleDao priceRuleDao;



    @Override
    public ICommonDao<PriceRule, Integer> getDao() {
        return priceRuleDao;
    }

    /**
     * 通过分类id获取定价规则列表
     *
     * @param id
     * @return
     */
    @Override
    public List<PriceRule> findByCategoryId(Integer id) {
        PriceRuleVO priceRuleVO = new PriceRuleVO();
        priceRuleVO.setCategoryId(id);
        return priceRuleDao.findByParameter(priceRuleVO);
    }
}
