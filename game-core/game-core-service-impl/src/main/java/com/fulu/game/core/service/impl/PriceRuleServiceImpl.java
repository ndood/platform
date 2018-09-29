package com.fulu.game.core.service.impl;


import com.fulu.game.common.utils.CollectionUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.PriceRuleVO;
import com.fulu.game.core.service.UserTechAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.PriceRuleDao;
import com.fulu.game.core.entity.PriceRule;
import com.fulu.game.core.service.PriceRuleService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class PriceRuleServiceImpl extends AbsCommonService<PriceRule,Integer> implements PriceRuleService {

    @Autowired
	private PriceRuleDao priceRuleDao;

    @Qualifier(value = "userTechAuthServiceImpl")
    @Autowired
    private UserTechAuthServiceImpl userTechAuthService;

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

    @Override
    public List<PriceRuleVO> findUserPriceByCategoryId(Integer categoryId,int userId) {
        List<UserTechAuth> userTechAuths =    userTechAuthService.findByCategoryAndUser(categoryId,userId);
        if(userTechAuths.isEmpty()){
            log.error("用户没有设置该分类的技能:categoryId:{}",categoryId);
            return new ArrayList<>();
        }
        UserTechAuth tech = userTechAuths.get(0);
        BigDecimal maxPrice = new BigDecimal(0);
        if(tech.getMaxPrice()!=null){
            maxPrice = tech.getMaxPrice();
        }
        List<PriceRule> priceRuleList = findByCategoryId(categoryId);
        List<PriceRuleVO> priceRuleVOList  = CollectionUtil.copyNewCollections(priceRuleList,PriceRuleVO.class);
        for(PriceRuleVO ruleVO : priceRuleVOList){
            if(maxPrice.compareTo(ruleVO.getPrice())>=0){
                ruleVO.setUsable(true);
            }else{
                ruleVO.setUsable(false);
                ruleVO.setMsg("订单完成数大于"+ruleVO.getOrderCount()+"单才可以设置此价格!");
            }
        }
        return priceRuleVOList;
    }


}
