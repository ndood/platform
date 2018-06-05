package com.fulu.game.core.service;

import com.fulu.game.core.entity.TechValue;

import java.util.List;
import java.util.Map;

/**
 * 技能属性值表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-18 16:39:55
 */
public interface TechValueService extends ICommonService<TechValue,Integer>{



     /**
      * 创建段位
      * @param categoryId
      * @param danName
      * @param rank
      * @return
      */
     TechValue createDan(Integer categoryId,String danName,Integer rank);

     TechValue updateDan(Integer id,String danName,Integer rank);


     List<TechValue> findByTechAttrId(Integer attrId);


}
