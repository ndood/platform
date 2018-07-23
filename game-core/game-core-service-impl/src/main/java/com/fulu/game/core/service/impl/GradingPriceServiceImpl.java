package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.GradingPriceDao;
import com.fulu.game.core.entity.GradingPrice;
import com.fulu.game.core.service.GradingPriceService;



@Service
public class GradingPriceServiceImpl extends AbsCommonService<GradingPrice,Integer> implements GradingPriceService {

    @Autowired
	private GradingPriceDao gradingPriceDao;



    @Override
    public ICommonDao<GradingPrice, Integer> getDao() {
        return gradingPriceDao;
    }
	
}
