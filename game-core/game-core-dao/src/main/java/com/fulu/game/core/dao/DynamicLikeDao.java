package com.fulu.game.core.dao;

import com.fulu.game.core.entity.DynamicLike;
import com.fulu.game.core.entity.vo.DynamicLikeVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 动态点赞表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-08-30 11:09:38
 */
@Mapper
public interface DynamicLikeDao extends ICommonDao<DynamicLike,Long>{

    List<DynamicLike> findByParameter(DynamicLikeVO dynamicLikeVO);

}
