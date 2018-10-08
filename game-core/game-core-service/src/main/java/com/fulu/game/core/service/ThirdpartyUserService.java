package com.fulu.game.core.service;

import com.fulu.game.core.entity.ThirdpartyUser;



/**
 * 第三方用户信息
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-08-15 11:56:59
 */
public interface ThirdpartyUserService extends ICommonService<ThirdpartyUser,Integer>{


    ThirdpartyUser findByFqlOpenid(String fqlOpenId);

    ThirdpartyUser findByUserId(Integer userId);


    ThirdpartyUser createFenqileUser(String fqlOpenId,String ip);
}
