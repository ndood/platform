package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserProfession;
import com.fulu.game.core.entity.vo.UserProfessionVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户职业表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-21 14:29:58
 */
@Mapper
public interface UserProfessionDao extends ICommonDao<UserProfession,Integer>{

    List<UserProfession> findByParameter(UserProfessionVO userProfessionVO);

}
