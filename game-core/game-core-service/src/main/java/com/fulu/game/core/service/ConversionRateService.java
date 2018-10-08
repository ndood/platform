package com.fulu.game.core.service;

import com.fulu.game.core.entity.ConversionRate;

import java.text.ParseException;


/**
 * 转换率统计表
 * 
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-25 11:52:10
 */
public interface ConversionRateService extends ICommonService<ConversionRate,Integer>{

    /**
     * 统计转化率
     */
    void statisticsConversionRate() throws ParseException;
}
