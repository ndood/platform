package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserBodyAuth;
import com.fulu.game.core.entity.vo.UserBodyAuthVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户身份认证信息表
 * @author jaycee.Deng
 * @email ${email}
 * @date 2018-09-05 17:40:37
 */
@Mapper
public interface UserBodyAuthDao extends ICommonDao<UserBodyAuth,Integer>{

    List<UserBodyAuth> findByParameter(UserBodyAuthVO userBodyAuthVO);

}
