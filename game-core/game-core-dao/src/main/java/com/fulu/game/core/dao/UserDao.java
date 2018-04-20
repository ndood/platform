package com.fulu.game.core.dao;

import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserVO;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表
 * @author wangbin
 * @email ${email}
 * @date 2018-04-20 11:12:12
 */
@Mapper
public interface UserDao extends ICommonDao<User,Integer>{

    List<User> findByParameter(UserVO userVO);

}
