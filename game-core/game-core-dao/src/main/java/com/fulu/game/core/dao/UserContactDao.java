package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserContact;
import com.fulu.game.core.entity.vo.UserContactVO;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-24 19:35:43
 */
@Mapper
public interface UserContactDao extends ICommonDao<UserContact,Integer>{

    List<UserContact> findByParameter(UserContactVO userContactVO);

    UserContact findByUserIdAndType(@Param(value = "userId") Integer userId,@Param(value = "type") Integer type);


    int updateOtherDefault(@Param(value = "userId") Integer userId,@Param(value = "updateTime")Date updateTime);
}
