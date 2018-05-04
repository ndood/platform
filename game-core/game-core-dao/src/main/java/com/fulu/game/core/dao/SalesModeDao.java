package com.fulu.game.core.dao;

import com.fulu.game.core.entity.SalesMode;
import com.fulu.game.core.entity.vo.SalesModeVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-05-04 18:48:12
 */
@Mapper
public interface SalesModeDao extends ICommonDao<SalesMode,Integer>{

    List<SalesMode> findByParameter(SalesModeVO salesModeVO);

}
