package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.CouponDao;
import com.fulu.game.core.entity.Coupon;
import com.fulu.game.core.service.CouponService;



@Service
public class CouponServiceImpl extends AbsCommonService<Coupon,Integer> implements CouponService {

    @Autowired
	private CouponDao couponDao;



    @Override
    public ICommonDao<Coupon, Integer> getDao() {
        return couponDao;
    }
	
}
