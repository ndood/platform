package com.fulu.game.core.service;

import com.fulu.game.core.entity.PriceFactor;



/**
 * 价格系数表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-06-25 18:07:42
 */
public interface PriceFactorService extends ICommonService<PriceFactor,Integer>{


    PriceFactor findByNewPriceFactor();

    /**
     * 获取CJ的价格系数表
     * @return
     */
    PriceFactor findCjPriceFactor();
}
