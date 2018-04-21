package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;


import org.apache.ibatis.annotations.Mapper;

/**
 * 信息认证表
 * @author wangbin
 * @email ${email}
 * @date 2018-04-20 11:12:13
 */
@Mapper
public interface UserInfoAuthDao extends ICommonDao<UserInfoAuth,Integer>{

    UserInfoAuth findByParameter(UserInfoAuthVO userInfoAuthVO);

}
