package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserTechAuthReject;

import java.util.List;


/**
 * 技能认证信息驳回表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-05-28 18:07:18
 */
public interface UserTechAuthRejectService extends ICommonService<UserTechAuthReject,Integer>{

    UserTechAuthReject findLastRecordByTechAuth(Integer techAuthId,Integer status);

    List<UserTechAuthReject> findByTechAuth(Integer techAuthId, Integer status);
}
