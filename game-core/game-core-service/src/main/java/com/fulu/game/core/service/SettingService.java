package com.fulu.game.core.service;

import com.fulu.game.core.entity.Setting;
import com.github.pagehelper.PageInfo;

import java.util.List;


/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-25 18:48:32
 */
public interface SettingService extends ICommonService<Setting,Integer>{



    public Setting lastSettingType(int type);


    public PageInfo<Setting> settingList(int pageNum, int pageSize,int type);
}
