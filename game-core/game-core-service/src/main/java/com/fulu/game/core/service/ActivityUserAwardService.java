package com.fulu.game.core.service;

import com.fulu.game.core.entity.ActivityUserAward;



/**
 * 用户活动奖励表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-09-28 14:32:06
 */
public interface ActivityUserAwardService extends ICommonService<ActivityUserAward,Integer>{


    ActivityUserAward findByUserAndActivity(Integer userId,Integer activityId);

}
