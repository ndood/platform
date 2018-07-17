package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.UserScoreDetailsDao;
import com.fulu.game.core.entity.UserScoreDetails;
import com.fulu.game.core.service.UserScoreDetailsService;



@Service
public class UserScoreDetailsServiceImpl extends AbsCommonService<UserScoreDetails,Integer> implements UserScoreDetailsService {

    @Autowired
	private UserScoreDetailsDao userScoreDetailsDao;



    @Override
    public ICommonDao<UserScoreDetails, Integer> getDao() {
        return userScoreDetailsDao;
    }
	
}
