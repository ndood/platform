package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.CategoryUnitDao;
import com.fulu.game.core.entity.CategoryUnit;
import com.fulu.game.core.service.CategoryUnitService;



@Service
public class CategoryUnitServiceImpl extends AbsCommonService<CategoryUnit,Integer> implements CategoryUnitService {

    @Autowired
	private CategoryUnitDao categoryUnitDao;



    @Override
    public ICommonDao<CategoryUnit, Integer> getDao() {
        return categoryUnitDao;
    }
	
}
