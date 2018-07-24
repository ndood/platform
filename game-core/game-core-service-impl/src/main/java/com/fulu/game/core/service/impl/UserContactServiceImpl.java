package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.UserContactDao;
import com.fulu.game.core.entity.UserContact;
import com.fulu.game.core.service.UserContactService;



@Service
public class UserContactServiceImpl extends AbsCommonService<UserContact,Integer> implements UserContactService {

    @Autowired
	private UserContactDao userContactDao;



    @Override
    public ICommonDao<UserContact, Integer> getDao() {
        return userContactDao;
    }
	
}
