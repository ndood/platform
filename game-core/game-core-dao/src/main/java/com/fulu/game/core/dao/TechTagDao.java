package com.fulu.game.core.dao;

import com.fulu.game.core.entity.TechTag;
import com.fulu.game.core.entity.vo.TechTagVO;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 技能标签关联表
 * @author wangbin
 * @email ${email}
 * @date 2018-04-23 13:56:51
 */
@Mapper
public interface TechTagDao extends ICommonDao<TechTag,Integer>{

    List<TechTag> findByParameter(TechTagVO techTagVO);

    int deleteByTechAuthId(Integer techAuthId);

}
