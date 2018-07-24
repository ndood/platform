package com.fulu.game.core.service;

import com.fulu.game.core.entity.GradingPrice;
import com.fulu.game.core.entity.vo.GradingPriceVO;

import java.math.BigDecimal;
import java.util.List;


/**
 * 段位定级表
 *
 * @author wangbin
 * @email ${email}
 * @date 2018-07-23 19:34:58
 */
public interface GradingPriceService extends ICommonService<GradingPrice, Integer> {


    GradingPrice create(Integer pid, String name, Integer rank, BigDecimal price);

    GradingPrice update(Integer id, String name, Integer rank, BigDecimal price);

    List<GradingPriceVO> findByGradingPrice(int pid);


    BigDecimal findRangePrice(int categoryId,int startGradingId, int endGradingId);
}
