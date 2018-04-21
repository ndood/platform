package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.fulu.game.core.dao.UserDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.UserService;



@Service
public class UserServiceImpl extends AbsCommonService<User,Integer> implements UserService {

    @Autowired
	private UserDao userDao;



    @Override
    public ICommonDao<User, Integer> getDao() {
        return userDao;
    }

    @Override
    public User findByMobile(String mobile) {
        UserVO userVO = new UserVO();
        userVO.setMobile(mobile);
        List<User> users =userDao.findByParameter(userVO);
        if(users!=null&&users.size()>0){
           return users.get(0);
        }
        return null;
    }
}
