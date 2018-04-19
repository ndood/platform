package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.TechAttrTypeEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.TechAttr;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.TechAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fulu.game.core.dao.TechValueDao;
import com.fulu.game.core.entity.TechValue;
import com.fulu.game.core.service.TechValueService;

import java.util.Date;


@Service
public class TechValueServiceImpl extends AbsCommonService<TechValue,Integer> implements TechValueService {

    @Autowired
	private TechValueDao techValueDao;
    @Autowired
    private TechAttrService techAttrService;
    @Autowired
    private CategoryService categoryService;

    @Override
    public ICommonDao<TechValue, Integer> getDao() {
        return techValueDao;
    }


    @Override
    public TechValue createSalesMode(Integer categoryId, String salesmodeName) {
        Category category = categoryService.findById(categoryId);
        TechAttr techAttr = techAttrService.findByCategoryAndType(categoryId, TechAttrTypeEnum.SALES_MODE.getType());
        if(techAttr==null){
            techAttr = new TechAttr();
            techAttr.setCategoryId(category.getId());
            techAttr.setName(category.getName()+"销售方式");
            techAttr.setType(TechAttrTypeEnum.SALES_MODE.getType());
            techAttr.setStatus(true);
            techAttr.setCreateTime(new Date());
            techAttr.setUpdateTime(new Date());
            techAttrService.create(techAttr);
        }
        TechValue techValue = new TechValue();
        techValue.setTechAttrId(techAttr.getId());
        techValue.setName(salesmodeName);
        techValue.setStatus(true);
        techValue.setCreateTime(new Date());
        techValue.setUpdateTime(new Date());
        create(techValue);
        return techValue;
    }
}
