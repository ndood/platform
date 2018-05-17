package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.CouponGrant;
import com.fulu.game.core.entity.vo.CouponGrantUserVO;
import com.fulu.game.core.entity.vo.CouponGrantVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.CouponGrantUserDao;
import com.fulu.game.core.entity.CouponGrantUser;
import com.fulu.game.core.service.CouponGrantUserService;

import java.util.Date;
import java.util.List;


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


    public List<CouponGrantUser> findByGrantId(Integer grantId){
        CouponGrantUserVO param = new CouponGrantUserVO();
        param.setCouponGrantId(grantId);
        return couponGrantUserDao.findByParameter(param);
    }


    @Override
    public PageInfo<CouponGrantUser> list(Integer grantId, Integer pageNum, Integer pageSize, String orderBy) {
        if(StringUtils.isBlank(orderBy)){
            orderBy = "id desc";
        }
        PageHelper.startPage(pageNum,pageSize,orderBy);
        List<CouponGrantUser> list = findByGrantId(grantId);
        PageInfo page = new PageInfo(list);
        return page;
    }
}
