package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.vo.ServerCardVO;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * 信息认证表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-20 11:12:13
 */
public interface UserInfoAuthService extends ICommonService<UserInfoAuth,Integer>{

    UserInfoAuthVO save(UserInfoAuthVO userInfoAuthVO);

    /**
     * 查找用户个人认证信息
     * @param userId
     * @return
     */
    UserInfoAuthVO findUserAuthInfoByUserId(Integer userId);

    PageInfo<UserInfoAuthVO> list(Integer pageNum, Integer pageSize, String orderBy);

    /**
     * 查询用户名片
     * @param userId
     * @return
     */
    ServerCardVO.UserInfo findUserCardByUserId(Integer userId);
}
