package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.UserInfoAuthFileTempDao;
import com.fulu.game.core.entity.UserInfoAuthFileTemp;
import com.fulu.game.core.service.UserInfoAuthFileTempService;



@Service
public class UserInfoAuthFileTempServiceImpl extends AbsCommonService<UserInfoAuthFileTemp,Integer> implements UserInfoAuthFileTempService {

    @Autowired
	private UserInfoAuthFileTempDao userInfoAuthFileTempDao;



    @Override
    public ICommonDao<UserInfoAuthFileTemp, Integer> getDao() {
        return userInfoAuthFileTempDao;
    }

    @Override
    public Integer deleteByUserId(Integer userId) {
        return userInfoAuthFileTempDao.deleteByUserId(userId);
    }
}
