package com.fulu.game.core.service;

import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.vo.CategoryVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 分类表
 *
 * @author wangbin
 * @email ${email}
 * @date 2018-04-18 15:55:39
 */
public interface CategoryService extends ICommonService<Category, Integer> {


    PageInfo<Category> list(int pageNum, int pageSize);

    PageInfo<Category> list(int pageNum, int pageSize, Boolean status, String orderBy);

    CategoryVO findCategoryVoById(Integer id);

    List<Category> findByPid(Integer pid, Boolean status);

    List<Category> findAllAccompanyPlayCategory();

    Category save(CategoryVO categoryVO);

    List<Category> findPointCategory();

    /**
     * 通过一级分类pid查询改分类下的所有子分类
     */
    List<Category> findByFirstPid(Integer pid, Boolean status);

    /**
     * 查询子分类是否属于父分类
     *
     * @param parentCategoryId
     * @param categoryId
     * @return
     */
    Boolean isInParentCategory(int parentCategoryId, int categoryId);

    /**
     * 获取迅雷约玩首页-分类
     *
     * @return 分类VO
     */
    CategoryVO findThunderCategory();
}
