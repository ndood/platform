package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.SettingVO;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.SettingDao;
import com.fulu.game.core.entity.Setting;
import com.fulu.game.core.service.SettingService;

import java.util.List;


@Service
public class SettingServiceImpl extends AbsCommonService<Setting,Integer> implements SettingService {

    @Autowired
	private SettingDao settingDao;



    @Override
    public ICommonDao<Setting, Integer> getDao() {
        return settingDao;
    }


    public Setting lastSettingType(int type){
        PageHelper.startPage(1,1,"id desc");
        SettingVO param = new SettingVO();
        param.setType(type);
        List<Setting> settingList =    settingDao.findByParameter(param);
        if(settingList.isEmpty()){
            return null;
        }
        return settingList.get(0);
    }

	
}
