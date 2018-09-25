package com.fulu.game.core.dao;

import com.fulu.game.core.entity.ConversionRate;
import com.fulu.game.core.entity.vo.ConversionRateVO;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 转换率统计表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-25 11:52:10
 */
@Mapper
public interface ConversionRateDao extends ICommonDao<ConversionRate,Integer>{

    List<ConversionRate> findByParameter(ConversionRateVO conversionRateVO);

    /**
     * 获取订单相关数量
     *
     */
    ConversionRate getOrderCount(ConversionRateVO params);

    /**
     * 获取新用户订单相关数据
     * @param tmp
     * @return
     */
    ConversionRate getNewOrderCount(ConversionRateVO tmp);

    /**
     * 将数据插入历史表
     */
    void selectInto();

    /**
     * 删除所有统计信息
     */
    void deleteAll();

    /**
     * 获取新人数
     * @param tmp
     * @return
     */
    ConversionRate getNewPeopleCount(ConversionRateVO tmp);
}
