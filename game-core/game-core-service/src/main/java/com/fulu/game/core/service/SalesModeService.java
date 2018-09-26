package com.fulu.game.core.service;

import com.fulu.game.core.entity.SalesMode;
import com.fulu.game.core.entity.vo.SalesModeVO;

import java.math.BigDecimal;
import java.util.List;


/**
 * @author wangbin
 * @email ${email}
 * @date 2018-05-04 18:48:12
 */
public interface SalesModeService extends ICommonService<SalesMode, Integer> {

    List<SalesMode> findByParameter(SalesModeVO salesModeVO);

    List<SalesMode> findByCategory(Integer categoryId);

    SalesMode update(Integer id, String name, BigDecimal price, Integer rank);
}
