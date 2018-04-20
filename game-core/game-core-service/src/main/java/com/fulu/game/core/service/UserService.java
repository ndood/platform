package com.fulu.game.core.service;

import com.fulu.game.core.entity.User;

import java.util.List;
import java.util.Map;

/**
 * 用户表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-20 11:12:12
 */
public interface UserService extends ICommonService<User,Integer>{


    public User findByMobile(String mobile);
	
}
