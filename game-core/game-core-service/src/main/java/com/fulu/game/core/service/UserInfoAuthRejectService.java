package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserInfoAuthReject;

import java.util.List;


/**
 * 用户认证信息驳回表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-05-28 11:29:00
 */
public interface UserInfoAuthRejectService extends ICommonService<UserInfoAuthReject,Integer>{


    UserInfoAuthReject findLastRecordByUserId(Integer userId,Integer status);

    List<UserInfoAuthReject> findByUserId(Integer userId,Integer status);

    List<UserInfoAuthReject> findByUserId(Integer userId);

    List<UserInfoAuthReject> findByUserInfoAuthId(Integer userInfoAuthId);
}
