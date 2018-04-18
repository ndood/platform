package com.fulu.game.core.dao;

import com.fulu.game.core.entity.TechAttr;

import java.util.List;
import java.util.Map;

import com.fulu.game.core.entity.vo.TechAttrVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 技能属性表
 * @author wangbin
 * @email ${email}
 * @date 2018-04-18 16:29:27
 */
@Mapper
public interface TechAttrDao extends ICommonDao<TechAttr,Integer>{

    TechAttr findByParameter(TechAttrVO techAttrVO);

}
