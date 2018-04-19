package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.CategoryParentEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.CategoryVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.fulu.game.core.dao.CategoryDao;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.service.CategoryService;



@Service
public class CategoryServiceImpl extends AbsCommonService<Category,Integer> implements CategoryService {

    @Autowired
	private CategoryDao categoryDao;



    @Override
    public ICommonDao<Category, Integer> getDao() {
        return categoryDao;
    }

    @Override
    public PageInfo<Category> list(int pageNum, int pageSize, Boolean status,String orderBy) {
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setStatus(status);
        categoryVO.setPid(CategoryParentEnum.ACCOMPANY_PLAY.getType());
        if(StringUtils.isNotBlank(orderBy)){
            orderBy = "sort desc";
        }
        PageHelper.startPage(pageNum,pageSize,orderBy);
        List<Category> categoryList = categoryDao.findByParameter(categoryVO);
        PageInfo page = new PageInfo(categoryList);
        return page;
    }
}
