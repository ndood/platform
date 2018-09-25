package com.fulu.game.core.service;

import com.fulu.game.core.entity.TechLevel;
import com.fulu.game.core.entity.vo.TechLevelVO;

import java.util.List;


/**
 * 技能等级表
 * 
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-25 11:47:10
 */
public interface TechLevelService extends ICommonService<TechLevel,Integer>{

    /**
     * 获取技能等级列表
     * @return
     */
    List<TechLevel> list();

    /**
     * 保存技能等级信息
     * @param techLevelVO
     */
    void save(TechLevelVO techLevelVO);
}
