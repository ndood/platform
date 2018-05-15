package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.CouponGroupDao;
import com.fulu.game.core.entity.CouponGroup;
import com.fulu.game.core.service.CouponGroupService;



@Service
public class CouponGroupServiceImpl extends AbsCommonService<CouponGroup,Integer> implements CouponGroupService {

    @Autowired
	private CouponGroupDao couponGroupDao;



    @Override
    public ICommonDao<CouponGroup, Integer> getDao() {
        return couponGroupDao;
    }
	
}
