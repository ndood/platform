package com.fulu.game.core.dao;

import com.fulu.game.core.entity.TechLevel;
import com.fulu.game.core.entity.vo.TechLevelVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 技能等级表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-25 11:47:10
 */
@Mapper
public interface TechLevelDao extends ICommonDao<TechLevel,Integer>{

    List<TechLevel> findByParameter(TechLevelVO techLevelVO);

}
