package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.ConversionRateDao;
import com.fulu.game.core.entity.ConversionRate;
import com.fulu.game.core.service.ConversionRateService;



@Service
public class ConversionRateServiceImpl extends AbsCommonService<ConversionRate,Integer> implements ConversionRateService {

    @Autowired
	private ConversionRateDao conversionRateDao;



    @Override
    public ICommonDao<ConversionRate, Integer> getDao() {
        return conversionRateDao;
    }
	
}
