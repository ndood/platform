package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.TechLevelDao;
import com.fulu.game.core.entity.TechLevel;
import com.fulu.game.core.service.TechLevelService;



@Service
public class TechLevelServiceImpl extends AbsCommonService<TechLevel,Integer> implements TechLevelService {

    @Autowired
	private TechLevelDao techLevelDao;



    @Override
    public ICommonDao<TechLevel, Integer> getDao() {
        return techLevelDao;
    }
	
}
