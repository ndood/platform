package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserProfession;

import java.util.List;


/**
 * 用户职业表
 * 
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-21 14:29:58
 */
public interface UserProfessionService extends ICommonService<UserProfession,Integer>{

    /**
     * 获取用户职业列表
     * @return
     */
    List<UserProfession> findUserProfessionList();
}
