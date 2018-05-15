package com.fulu.game.core.service.impl;


import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.CouponGroupDao;
import com.fulu.game.core.entity.CouponGroup;
import com.fulu.game.core.service.CouponGroupService;
import org.springframework.validation.annotation.Validated;

import java.util.Date;


@Service
public class CouponGroupServiceImpl extends AbsCommonService<CouponGroup,Integer> implements CouponGroupService {

    @Autowired
	private CouponGroupDao couponGroupDao;


    @Override
    public int create(CouponGroup couponGroup){
        couponGroup.setCreateTime(new Date());
        int result =  couponGroupDao.create(couponGroup);
        if(couponGroup.getAmount()==null){
            throw new ServiceErrorException("优惠券生成数量不能为空!");
        }



        return result;
    }


    @Override
    public ICommonDao<CouponGroup, Integer> getDao() {
        return couponGroupDao;
    }
	
}
