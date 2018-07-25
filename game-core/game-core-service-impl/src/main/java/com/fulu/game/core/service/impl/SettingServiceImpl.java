package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.SettingDao;
import com.fulu.game.core.entity.Setting;
import com.fulu.game.core.service.SettingService;



@Service
public class SettingServiceImpl extends AbsCommonService<Setting,Integer> implements SettingService {

    @Autowired
	private SettingDao settingDao;



    @Override
    public ICommonDao<Setting, Integer> getDao() {
        return settingDao;
    }
	
}
