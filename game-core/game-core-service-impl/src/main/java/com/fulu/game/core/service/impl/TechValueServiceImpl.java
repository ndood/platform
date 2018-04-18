package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.fulu.game.core.dao.TechValueDao;
import com.fulu.game.core.entity.TechValue;
import com.fulu.game.core.service.TechValueService;



@Service
public class TechValueServiceImpl extends AbsCommonService<TechValue,Integer> implements TechValueService {

    @Autowired
	private TechValueDao techValueDao;



    @Override
    public ICommonDao<TechValue, Integer> getDao() {
        return techValueDao;
    }
	
}
