package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
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
	
}
