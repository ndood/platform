package com.fulu.game.core.service;

import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserVO;
import com.github.pagehelper.PageInfo;

/**
 * 用户表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-20 11:12:12
 */
public interface UserService extends ICommonService<User,Integer>{

    User findByMobile(String mobile);
    void lock(int id);
    void unlock(int id);
    PageInfo<User> list(UserVO userVO, Integer pageNum, Integer pageSize);
    User save(UserVO userVO);
}
