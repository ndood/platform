package com.fulu.game.core.dao;

import com.fulu.game.core.entity.ImUser;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户表
 * @author wangbin
 * @email ${email}
 * @date 2018-04-20 11:12:12
 */
@Mapper
public interface UserDao extends ICommonDao<User,Integer>{

    List<User> findByParameter(UserVO userVO);

    int countByParameter(UserVO userVO);

    List<User> findByUserIds(@Param(value = "userIds") List<Integer> userIds);

    List<ImUser> findImNullUser();


    List<ImUser> findImNullUser();
}
