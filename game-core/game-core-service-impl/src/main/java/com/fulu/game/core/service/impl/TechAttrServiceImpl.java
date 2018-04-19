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
        TechAttr techAttr = techAttrDao.findByParameter(techAttrVO);
        return techAttr;
    }
}
