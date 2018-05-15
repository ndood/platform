package com.fulu.game.core.service.impl;


import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.CouponGroupDao;
import com.fulu.game.core.entity.CouponGroup;
import com.fulu.game.core.service.CouponGroupService;
import org.springframework.validation.annotation.Validated;

import java.util.Date;


@Service
public class CouponGroupServiceImpl implements CouponGroupService {

    @Autowired
	private CouponGroupDao couponGroupDao;
    @Autowired
    private CouponService couponService;


    @Override
    public int create(CouponGroup couponGroup){
        couponGroup.setCreateTime(new Date());
        int result =  couponGroupDao.create(couponGroup);

        return result;
    }


    public int batchGenerateCoupon(CouponGroup couponGroup){
        return 0;
    }
	
}
