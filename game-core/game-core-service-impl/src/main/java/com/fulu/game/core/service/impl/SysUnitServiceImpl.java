package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.SysUnitDao;
import com.fulu.game.core.entity.SysUnit;
import com.fulu.game.core.service.SysUnitService;



@Service
public class SysUnitServiceImpl extends AbsCommonService<SysUnit,Integer> implements SysUnitService {

    @Autowired
	private SysUnitDao sysUnitDao;



    @Override
    public ICommonDao<SysUnit, Integer> getDao() {
        return sysUnitDao;
    }
	
}
