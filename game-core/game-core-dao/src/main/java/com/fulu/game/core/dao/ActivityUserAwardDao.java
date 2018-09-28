package com.fulu.game.core.dao;

import com.fulu.game.core.entity.ActivityUserAward;
import com.fulu.game.core.entity.vo.ActivityUserAwardVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户活动奖励表
 * @author wangbin
 * @email ${email}
 * @date 2018-09-28 14:32:06
 */
@Mapper
public interface ActivityUserAwardDao extends ICommonDao<ActivityUserAward,Integer>{

    List<ActivityUserAward> findByParameter(ActivityUserAwardVO activityUserAwardVO);

}
