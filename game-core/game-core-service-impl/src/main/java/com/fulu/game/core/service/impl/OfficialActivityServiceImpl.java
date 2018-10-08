package com.fulu.game.core.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.enums.ActivityTypeEnum;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.exception.CouponException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.OfficialActivityDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.OfficialActivityVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class OfficialActivityServiceImpl extends AbsCommonService<OfficialActivity, Integer> implements OfficialActivityService {

    @Autowired
    private OfficialActivityDao officialActivityDao;
    @Autowired
    private CouponGroupService couponGroupService;
    @Autowired
    private ActivityCouponService activityCouponService;
    @Autowired
    private ActivityUserAwardService activityUserAwardService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CouponOpenService couponOpenService;
    @Autowired
    private AdminService adminService;

    public OfficialActivity create(int type, OfficialActivity activity, List<String> redeemCodes) {
        if (ActivityTypeEnum.COUPON.getType().equals(type)) {
            if (redeemCodes == null) {
                throw new IllegalArgumentException("优惠券活动优惠券编码不能为空!");
            }
        }
        Admin admin = adminService.getCurrentUser();

        activity.setIsActivate(false);
        activity.setCreateTime(new Date());
        activity.setUpdateTime(new Date());
        activity.setAdminId(admin.getId());
        activity.setAdminName(admin.getName());
        activity.setIsDel(false);
        create(activity);
        for (String redeemCode : redeemCodes) {
            CouponGroup couponGroup = couponGroupService.findByRedeemCode(redeemCode);
            if (couponGroup == null) {
                throw new CouponException(CouponException.ExceptionCode.REDEEMCODE_ERROR);
            }
            ActivityCoupon activityCoupon = new ActivityCoupon();
            activityCoupon.setCouponGroupId(couponGroup.getId());
            activityCoupon.setRedeemCode(couponGroup.getRedeemCode());
            activityCoupon.setOfficialActivityId(activity.getId());
            activityCoupon.setCategoryId(couponGroup.getCategoryId());
            activityCoupon.setDeduction(couponGroup.getDeduction());
            activityCoupon.setFullReduction(couponGroup.getFullReduction());
            activityCoupon.setCouponType(couponGroup.getType());
            activityCoupon.setStartUsefulTime(couponGroup.getStartUsefulTime());
            activityCoupon.setEndUsefulTime(couponGroup.getEndUsefulTime());
            Category category = categoryService.findById(activityCoupon.getCategoryId());
            activityCoupon.setCategoryName(category.getName());
            activityCoupon.setCreateTime(new Date());
            activityCoupon.setUpdateTime(new Date());
            activityCouponService.create(activityCoupon);
        }
        return activity;
    }

    @Override
    public void delete(int activityId) {
        OfficialActivity activity = findById(activityId);
        activity.setIsDel(true);
        update(activity);
    }

    @Override
    public boolean activate(int activityId, boolean activate) {
        OfficialActivity activity = findById(activityId);
        activity.setIsActivate(activate);
        update(activity);
        return activate;
    }


    @Override
    public PageInfo<OfficialActivity> list(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize,"id desc");
        List<OfficialActivity> list = getDao().findAll();
        PageInfo page = new PageInfo(list);
        return page;
    }


    /**
     * 获得可用的活动
     * @return
     */
    public OfficialActivityVO getUsableActivity(PlatformEcoEnum platformEcoEnum) {
        OfficialActivityVO param = new OfficialActivityVO();
        param.setCurrentTime(new Date());
        param.setIsActivate(true);
        param.setPlatform(platformEcoEnum.getType());
        List<OfficialActivity> officialActivities = officialActivityDao.findByParameter(param);
        if (officialActivities.isEmpty()) {
            return null;
        }
        OfficialActivityVO officialActivityVO = new OfficialActivityVO();
        BeanUtil.copyProperties(officialActivities.get(0), officialActivityVO);
        if (officialActivityVO.getType().equals(ActivityTypeEnum.COUPON.getType())) {
            List<ActivityCoupon> activityCouponList = activityCouponService.findByActivityId(officialActivityVO.getId());
            officialActivityVO.setCouponList(activityCouponList);
        }
        return officialActivityVO;
    }

    /**
     * 用户参加活动
     * @return
     */
    public Boolean userJoinActivity(Integer userId, Integer activityId,String userIp) {
        ActivityUserAward activityUserAward = activityUserAwardService.findByUserAndActivity(userId, activityId);
        if (activityUserAward != null) {
            return false;
        }
        activityUserAward = new ActivityUserAward();
        activityUserAward.setActivityId(activityId);
        activityUserAward.setUserId(userId);
        activityUserAward.setCreateTime(new Date());
        activityUserAwardService.create(activityUserAward);
        List<ActivityCoupon> activityCouponList = activityCouponService.findByActivityId(activityId);
        for(ActivityCoupon coupon : activityCouponList){
            couponOpenService.generateCoupon(coupon.getRedeemCode(),userId,new Date(),userIp);
        }
        return true;
    }


    @Override
    public ICommonDao<OfficialActivity, Integer> getDao() {
        return officialActivityDao;
    }

}
