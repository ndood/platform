package com.fulu.game.core.service.impl;


import com.fulu.game.common.Constant;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.PriceFactorDao;
import com.fulu.game.core.entity.PriceFactor;
import com.fulu.game.core.entity.vo.PriceFactorVO;
import com.fulu.game.core.service.PriceFactorService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PriceFactorServiceImpl extends AbsCommonService<PriceFactor, Integer> implements PriceFactorService {

    @Autowired
    private PriceFactorDao priceFactorDao;


    @Override
    public ICommonDao<PriceFactor, Integer> getDao() {
        return priceFactorDao;
    }

    @Override
    public PriceFactor findByNewPriceFactor() {
        PageHelper.startPage(1, 1, "id desc");
        PriceFactorVO priceFactorVO = new PriceFactorVO();
        priceFactorVO.setSourceType(Constant.PRICE_FACTOR_TYPE_LINGHANG);
        List<PriceFactor> list = priceFactorDao.findByParameter(priceFactorVO);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 获取CJ的价格系数
     *
     * @return 价格系数Bean
     */
    @Override
    public PriceFactor findCjPriceFactor() {
        PriceFactorVO priceFactorVO = new PriceFactorVO();
        priceFactorVO.setSourceType(Constant.PRICE_FACTOR_TYPE_CHINAJOY);
        List<PriceFactor> list = priceFactorDao.findByParameter(priceFactorVO);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}
