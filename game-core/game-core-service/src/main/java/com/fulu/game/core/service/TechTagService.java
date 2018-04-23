package com.fulu.game.core.service;

import com.fulu.game.core.entity.TechTag;

import java.util.List;
import java.util.Map;

/**
 * 技能标签关联表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-23 13:56:51
 */
public interface TechTagService extends ICommonService<TechTag,Integer>{

    int deleteByTechAuthId(Integer techAuthId);

    List<TechTag> findByTechAuthId(Integer techAuthId);
}
