package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.ActivityCouponVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.ActivityCouponDao;
import com.fulu.game.core.entity.ActivityCoupon;
import com.fulu.game.core.service.ActivityCouponService;

import java.util.List;


@Service
public class ActivityCouponServiceImpl extends AbsCommonService<ActivityCoupon,Integer> implements ActivityCouponService {

    @Autowired
	private ActivityCouponDao activityCouponDao;



    @Override
    public ICommonDao<ActivityCoupon, Integer> getDao() {
        return activityCouponDao;
    }



    @Override
    public List<ActivityCoupon> findByActivityId(Integer activityId) {
        ActivityCouponVO param = new ActivityCouponVO();
        param.setOfficialActivityId(activityId);
        return activityCouponDao.findByParameter(param);
    }


}
