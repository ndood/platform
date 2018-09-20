package com.fulu.game.core.service;

import com.fulu.game.core.entity.SalesMode;

import java.math.BigDecimal;
import java.util.List;


/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-05-04 18:48:12
 */
public interface SalesModeService extends ICommonService<SalesMode,Integer>{


    List<SalesMode> findByCategory(Integer categoryId);

    List<SalesMode> findByCategoryAndPlatformShow(Integer categoryId,List<Integer> platForm);

    SalesMode update(Integer id,String name,BigDecimal price,Integer rank);
}
