package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.vo.CategoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类表
 *
 * @author wangbin
 * @email ${email}
 * @date 2018-04-18 15:55:39
 */
@Mapper
public interface CategoryDao extends ICommonDao<Category, Integer> {


    List<Category> findByParameter(CategoryVO categoryVO);

    /**
     * 通过一级分类获取分类列表
     */
    List<Category> findByFirstPidAndPrams(CategoryVO categoryVO);

    List<Category> findByIdList(@Param(value = "ids") List<Integer> ids);
}
