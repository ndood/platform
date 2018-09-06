package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserBodyAuth;
import com.fulu.game.core.entity.vo.UserBodyAuthVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户身份认证信息表
 *
 * @author jaycee Deng
 * @email ${email}
 * @date 2018-09-06 14:29:30
 */
@Mapper
public interface UserBodyAuthDao extends ICommonDao<UserBodyAuth, Integer> {

    List<UserBodyAuth> findByParameter(UserBodyAuthVO userBodyAuthVO);

    UserBodyAuth findByUserId(Integer userId);
}
