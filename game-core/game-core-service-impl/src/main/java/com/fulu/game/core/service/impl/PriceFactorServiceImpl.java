package com.fulu.game.core.service.impl;


import com.fulu.game.common.utils.CollectionUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.PriceFactorDao;
import com.fulu.game.core.entity.PriceFactor;
import com.fulu.game.core.service.PriceFactorService;

import java.util.List;


@Service
public class PriceFactorServiceImpl extends AbsCommonService<PriceFactor,Integer> implements PriceFactorService {

    @Autowired
	private PriceFactorDao priceFactorDao;



    @Override
    public ICommonDao<PriceFactor, Integer> getDao() {
        return priceFactorDao;
    }

    @Override
    public PriceFactor findByNewPriceFactor() {
        PageHelper.startPage(1,1,"id desc");
        List<PriceFactor> list =  priceFactorDao.findAll();
        if(list.isEmpty()){
            return null;
        }
        return list.get(0);
    }


}
