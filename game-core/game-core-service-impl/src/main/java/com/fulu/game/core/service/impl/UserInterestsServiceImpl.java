package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.UserInterestsDao;
import com.fulu.game.core.entity.UserInterests;
import com.fulu.game.core.service.UserInterestsService;


/**
 * 用户兴趣
 */
@Service("userInterestsService")
public class UserInterestsServiceImpl extends AbsCommonService<UserInterests,Integer> implements UserInterestsService {

    @Autowired
	private UserInterestsDao userInterestsDao;

    @Override
    public ICommonDao<UserInterests, Integer> getDao() {
        return userInterestsDao;
    }
	
}
