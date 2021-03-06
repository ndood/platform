package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.TechAttrTypeEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.TechAttr;
import com.fulu.game.core.entity.vo.TechValueVO;
import com.fulu.game.core.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fulu.game.core.dao.TechValueDao;
import com.fulu.game.core.entity.TechValue;

import java.util.ArrayList;
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
    @Autowired
    private UserTechInfoService userTechInfoService;

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
            techAttr.setName("实力");
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
    public TechValue updateAttrVal(Integer id, String danName, Integer rank) {
        TechValue techValue =findById(id);
        techValue.setName(danName);
        techValue.setRank(rank);
        update(techValue);
        userTechInfoService.updateUserTechInfoByTechValue(techValue);
        return techValue;
    }

    @Override
    public TechValue createArea(Integer categoryId, String danName, Integer rank) {
        Category category = categoryService.findById(categoryId);
        TechAttr techAttr =techAttrService.findByCategoryAndType(categoryId,TechAttrTypeEnum.AREA.getType());
        if(techAttr==null){
            techAttr = new TechAttr();
            techAttr.setCategoryId(category.getId());
            techAttr.setName("大区");
            techAttr.setType(TechAttrTypeEnum.AREA.getType());
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
    public List<TechValue> areaList(Integer categoryId) {
        TechAttr techAttr =techAttrService.findByCategoryAndType(categoryId,TechAttrTypeEnum.AREA.getType());
        if(techAttr==null){
            return new ArrayList<>();
        }
        return findByTechAttrId(techAttr.getId());
    }


    @Override
    public List<TechValue> findByTechAttrId(Integer attrId) {
        TechValueVO techValueVO = new TechValueVO();
        techValueVO.setTechAttrId(attrId);
        List<TechValue> techValues =techValueDao.findByParameter(techValueVO);
        return techValues;
    }
}
