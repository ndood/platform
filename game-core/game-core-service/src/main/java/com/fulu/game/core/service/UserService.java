package com.fulu.game.core.service;

import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserVO;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户表
 * @author wangbin
 * @date 2018-04-20 11:12:12
 */
public interface UserService extends ICommonService<User,Integer>{

    User findByMobile(String mobile);
    User findByOpenId(String openId);
    void lock(int id);
    void unlock(int id);
    PageInfo<User> list(UserVO userVO, Integer pageNum, Integer pageSize);
    User save(UserVO userVO);



}
