package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.UserProfessionDao;
import com.fulu.game.core.entity.UserProfession;
import com.fulu.game.core.service.UserProfessionService;

import java.util.List;


@Service
public class UserProfessionServiceImpl extends AbsCommonService<UserProfession,Integer> implements UserProfessionService {

    @Autowired
	private UserProfessionDao userProfessionDao;



    @Override
    public ICommonDao<UserProfession, Integer> getDao() {
        return userProfessionDao;
    }

    /**
     * 获取用户职业列表
     *
     * @return
     */
    @Override
    public List<UserProfession> findUserProfessionList() {
        return userProfessionDao.findAll();
    }
}
