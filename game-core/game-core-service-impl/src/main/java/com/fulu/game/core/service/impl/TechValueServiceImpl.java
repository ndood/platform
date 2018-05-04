package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.TechAttrTypeEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.TechAttr;
import com.fulu.game.core.entity.vo.TechValueVO;
import com.fulu.game.core.service.CategoryService;
import com.fulu.game.core.service.TechAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fulu.game.core.dao.TechValueDao;
import com.fulu.game.core.entity.TechValue;
import com.fulu.game.core.service.TechValueService;

import java.util.Date;
import java.util.List;


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




    /**
     * 创建段位
     * @param categoryId
     * @param danName
     * @param rank
     * @return
     */
    @Override
    public TechValue createDan(Integer categoryId, String danName, Integer rank) {
        Category category = categoryService.findById(categoryId);
        TechAttr techAttr =techAttrService.findByCategoryAndType(categoryId,TechAttrTypeEnum.DAN.getType());
        if(techAttr==null){
            techAttr = new TechAttr();
            techAttr.setCategoryId(category.getId());
            techAttr.setName(category.getName()+"段位");
            techAttr.setType(TechAttrTypeEnum.DAN.getType());
            techAttr.setStatus(true);
            techAttr.setCreateTime(new Date());
            techAttr.setUpdateTime(new Date());
            techAttrService.create(techAttr);
        }
        TechValue techValue = new TechValue();
        techValue.setTechAttrId(techAttr.getId());
        techValue.setName(danName);
        techValue.setRank(rank);
        techValue.setStatus(false);
        techValue.setCreateTime(new Date());
        techValue.setUpdateTime(new Date());
        create(techValue);
        return techValue;
    }

    @Override
    public List<TechValue> findByTechAttrId(Integer attrId) {
        TechValueVO techValueVO = new TechValueVO();
        techValueVO.setTechAttrId(attrId);
        List<TechValue> techValues =techValueDao.findByParameter(techValueVO);
        return techValues;
    }
}
