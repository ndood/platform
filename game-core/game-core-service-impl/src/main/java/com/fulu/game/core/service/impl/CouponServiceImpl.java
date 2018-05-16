package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.CouponDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Coupon;
import com.fulu.game.core.entity.vo.CouponVO;
import com.fulu.game.core.service.CouponService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CouponServiceImpl extends AbsCommonService<Coupon,Integer> implements CouponService {

    @Autowired
	private CouponDao couponDao;


    @Override
    public ICommonDao<Coupon, Integer> getDao() {
        return couponDao;
    }


    @Override
    public PageInfo<Coupon> listByGroup(Integer couponGroupId,
                                        Integer pageNum,
                                        Integer pageSize,
                                        String orderBy) {
        CouponVO param = new CouponVO();
        param.setCouponGroupId(couponGroupId);
        if(StringUtils.isBlank(orderBy)){
            orderBy = "receive_time desc";
        }
        PageHelper.startPage(pageNum,pageSize,orderBy);
        List<Coupon> couponList = couponDao.findByParameter(param);
        return new PageInfo<>(couponList);
    }

    @Override
    public Integer countByNotReceive(Integer couponGroupId) {
        return couponDao.countByNotReceive(couponGroupId);
    }

    @Override
    public List<Coupon> findByUserReceive(Integer couponGroupId, Integer userId) {
        return couponDao.findByUserReceive(couponGroupId, userId);
    }

}
