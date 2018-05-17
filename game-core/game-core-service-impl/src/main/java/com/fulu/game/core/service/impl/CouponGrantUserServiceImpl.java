package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.CouponGrantUserDao;
import com.fulu.game.core.entity.CouponGrantUser;
import com.fulu.game.core.service.CouponGrantUserService;

import java.util.Date;


@Service
public class CouponGrantUserServiceImpl extends AbsCommonService<CouponGrantUser,Integer> implements CouponGrantUserService {

    @Autowired
	private CouponGrantUserDao couponGrantUserDao;



    @Override
    public ICommonDao<CouponGrantUser, Integer> getDao() {
        return couponGrantUserDao;
    }

    @Override
    public int create(Integer couponGrantId, Integer userId, String mobile, Boolean isSuccess, String errorCause) {
        CouponGrantUser couponGrantUser = new CouponGrantUser();
        couponGrantUser.setCouponGrantId(couponGrantId);
        couponGrantUser.setErrorCause(errorCause);
        couponGrantUser.setUserId(userId);
        couponGrantUser.setMobile(mobile);
        couponGrantUser.setIsSuccess(isSuccess);
        couponGrantUser.setErrorCause(errorCause);
        couponGrantUser.setCreateTime(new Date());
        return create(couponGrantUser);
    }
}
