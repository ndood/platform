package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
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
	
}
