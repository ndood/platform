package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Setting;
import com.fulu.game.core.entity.vo.SettingVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-25 18:48:32
 */
@Mapper
public interface SettingDao extends ICommonDao<Setting,Integer>{

    List<Setting> findByParameter(SettingVO settingVO);

}
