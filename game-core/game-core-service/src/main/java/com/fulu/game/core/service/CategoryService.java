package com.fulu.game.core.service;

import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.TechValue;
import com.fulu.game.core.entity.vo.CategoryVO;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * 分类表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-18 15:55:39
 */
public interface CategoryService extends ICommonService<Category,Integer>{


     PageInfo<Category> list(int pageNum, int pageSize);

     PageInfo<Category> list(int pageNum, int pageSize, Boolean status, String orderBy);

     CategoryVO findCategoryVoById(Integer id);

     List<Category> findByPid(Integer pid,Boolean status);

     List<Category> findAllAccompanyPlayCategory();


}
