package com.fulu.game.core.service;

import com.fulu.game.core.entity.TechAttr;
import com.fulu.game.core.entity.TechValue;

import java.util.List;
import java.util.Map;

/**
 * 技能属性表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-18 16:29:27
 */
public interface TechAttrService extends ICommonService<TechAttr,Integer>{


     TechAttr findByCategoryAndType(Integer categoryId,Integer type);

     List<TechAttr> findByCategory(Integer categoryId);

     List<TechValue> findValByCategoryAndType(Integer categoryId,Integer type);
}
