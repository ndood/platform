package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.CouponGrantUserDao;
import com.fulu.game.core.entity.CouponGrantUser;
import com.fulu.game.core.service.CouponGrantUserService;



@Service
public class CouponGrantUserServiceImpl extends AbsCommonService<CouponGrantUser,Integer> implements CouponGrantUserService {

    @Autowired
	private CouponGrantUserDao couponGrantUserDao;



    @Override
    public ICommonDao<CouponGrantUser, Integer> getDao() {
        return couponGrantUserDao;
    }
	
}
