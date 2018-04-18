package com.fulu.game.core.dao;

import com.fulu.game.core.entity.TechValue;
import com.fulu.game.core.entity.vo.TechValueVO;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 技能属性值表
 * @author wangbin
 * @email ${email}
 * @date 2018-04-18 16:39:55
 */
@Mapper
public interface TechValueDao extends ICommonDao<TechValue,Integer>{

    TechValue findByParameter(TechValueVO techValueVO);

}
