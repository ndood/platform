package com.fulu.game.core.service.impl;

import com.fulu.game.common.threadpool.SpringThreadPoolExecutor;
import com.fulu.game.core.entity.Coupon;
import com.fulu.game.core.entity.vo.CouponGroupVO;
import com.fulu.game.core.service.CouponService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.CouponGroupDao;
import com.fulu.game.core.entity.CouponGroup;
import com.fulu.game.core.service.CouponGroupService;

import java.util.Date;
import java.util.List;


@Service
public class CouponGroupServiceImpl implements CouponGroupService {

    @Autowired
	private CouponGroupDao couponGroupDao;
    @Autowired
    private CouponService couponService;


    @Autowired
    private SpringThreadPoolExecutor springThreadPoolExecutor;

    @Override
    public int create(CouponGroup couponGroup){
        couponGroup.setCreateTime(new Date());
        int result =  couponGroupDao.create(couponGroup);
        return result;
    }

    @Override
    public CouponGroup findById(Integer id) {
        return couponGroupDao.findById(id);
    }


    @Override
    public CouponGroup findByRedeemCode(String redeemCode) {
        CouponGroupVO param = new CouponGroupVO();
        param.setRedeemCode(redeemCode);
        List<CouponGroup> couponGroupList = couponGroupDao.findByParameter(param);
        if(couponGroupList.isEmpty()){
            return null;
        }
        return couponGroupList.get(0);
    }

    @Override
    public PageInfo<CouponGroup> list(Integer pageNum, Integer pageSize, String orderBy) {
        if(StringUtils.isBlank(orderBy)){
            orderBy = "create_time desc";
        }
        PageHelper.startPage(pageNum,pageSize,orderBy);
        List<CouponGroup> couponGroupList =  couponGroupDao.findAll();
        PageInfo page = new PageInfo(couponGroupList);
        return page;
    }



	
}
