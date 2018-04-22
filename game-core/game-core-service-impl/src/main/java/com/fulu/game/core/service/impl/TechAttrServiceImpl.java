package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.TechAttrVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.fulu.game.core.dao.TechAttrDao;
import com.fulu.game.core.entity.TechAttr;
import com.fulu.game.core.service.TechAttrService;



@Service
public class TechAttrServiceImpl extends AbsCommonService<TechAttr,Integer> implements TechAttrService {

    @Autowired
	private TechAttrDao techAttrDao;



    @Override
    public ICommonDao<TechAttr, Integer> getDao() {
        return techAttrDao;
    }

    @Override
    public TechAttr findByCategoryAndType(Integer categoryId, Integer type) {
        TechAttrVO techAttrVO = new TechAttrVO();
        techAttrVO.setCategoryId(categoryId);
        techAttrVO.setType(type);
        List<TechAttr> techAttrList = techAttrDao.findByParameter(techAttrVO);
        if(techAttrList!=null&&techAttrList.size()>0){
            return techAttrList.get(0);
        }
        return null;
    }

    @Override
    public List<TechAttr> findByCategory(Integer categoryId) {
        TechAttrVO techAttrVO = new TechAttrVO();
        techAttrVO.setCategoryId(categoryId);
        List<TechAttr> techAttrList = techAttrDao.findByParameter(techAttrVO);
        return techAttrList;
    }
}
