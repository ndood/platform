package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserInfoAuthReject;
import com.fulu.game.core.entity.vo.UserInfoAuthRejectVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户认证信息驳回表
 * @author wangbin
 * @email ${email}
 * @date 2018-05-28 11:29:00
 */
@Mapper
public interface UserInfoAuthRejectDao extends ICommonDao<UserInfoAuthReject,Integer>{

    List<UserInfoAuthReject> findByParameter(UserInfoAuthRejectVO userInfoAuthRejectVO);

}
