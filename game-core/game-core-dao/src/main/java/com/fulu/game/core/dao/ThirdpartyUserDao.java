package com.fulu.game.core.dao;

import com.fulu.game.core.entity.ThirdpartyUser;
import com.fulu.game.core.entity.vo.ThirdpartyUserVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 第三方用户信息
 * @author wangbin
 * @email ${email}
 * @date 2018-08-15 11:56:59
 */
@Mapper
public interface ThirdpartyUserDao extends ICommonDao<ThirdpartyUser,Integer>{

    List<ThirdpartyUser> findByParameter(ThirdpartyUserVO thirdpartyUserVO);

}
