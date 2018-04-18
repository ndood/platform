package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Category;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 分类表
 * @author wangbin
 * @email ${email}
 * @date 2018-04-18 15:55:39
 */
@Mapper
public interface CategoryDao extends ICommonDao<Category,Integer>{

}
