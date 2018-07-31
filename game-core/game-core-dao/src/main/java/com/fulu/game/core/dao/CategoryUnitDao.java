package com.fulu.game.core.dao;

import com.fulu.game.core.entity.CategoryUnit;
import com.fulu.game.core.entity.vo.CategoryUnitVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 游戏单位
 * @author wangbin
 * @email ${email}
 * @date 2018-07-31 11:18:12
 */
@Mapper
public interface CategoryUnitDao extends ICommonDao<CategoryUnit,Integer>{

    List<CategoryUnit> findByParameter(CategoryUnitVO categoryUnitVO);

}
