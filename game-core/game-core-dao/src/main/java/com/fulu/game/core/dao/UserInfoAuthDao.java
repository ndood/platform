package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.entity.vo.searchVO.UserInfoAuthSearchVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 信息认证表
 *
 * @author wangbin
 * @date 2018-04-20 11:12:13
 */
@Mapper
public interface UserInfoAuthDao extends ICommonDao<UserInfoAuth, Integer> {

    List<UserInfoAuth> findByParameter(UserInfoAuthVO userInfoAuthVO);

    List<UserInfoAuth> findBySearchVO(UserInfoAuthSearchVO userInfoAuthSearchVO);

    List<UserInfoAuth> findByUserIds(@Param(value = "userIds") List<Integer> userIds);

    /**
     * 根据userId更新用户认证信息
     * @param userInfoAuth 用户认证信息bean
     * @return 数据库返回操作结果
     */
    int updateByUserId(UserInfoAuth userInfoAuth);
}
