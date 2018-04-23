package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserTechInfo;

import java.util.List;
import java.util.Map;

/**
 * 用户技能具体信息表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-23 11:17:40
 */
public interface UserTechInfoService extends ICommonService<UserTechInfo,Integer>{


    int deleteByTechAuthId(Integer techAuthId);

    public List<UserTechInfo> findByTechAuthId(Integer techAuthId);
}
