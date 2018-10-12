package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.ThunderCodeEnum;
import com.fulu.game.common.exception.ThunderCodeException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.ThunderCodeDao;
import com.fulu.game.core.entity.Coupon;
import com.fulu.game.core.entity.ThunderCode;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.OrderVO;
import com.fulu.game.core.entity.vo.ThunderCodeVO;
import com.fulu.game.core.service.CouponOpenService;
import com.fulu.game.core.service.OrderService;
import com.fulu.game.core.service.ThunderCodeService;
import com.fulu.game.core.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class ThunderCodeServiceImpl extends AbsCommonService<ThunderCode, Integer> implements ThunderCodeService {

    @Autowired
    private ThunderCodeDao thunderCodeDao;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private CouponOpenService couponOpenService;

    @Override
    public ICommonDao<ThunderCode, Integer> getDao() {
        return thunderCodeDao;
    }


    public ThunderCode findByCode(String code, Integer type) {
        ThunderCodeVO vo = new ThunderCodeVO();
        vo.setCode(code);
        vo.setType(type);
        List<ThunderCode> codeList = thunderCodeDao.findByParameter(vo);
        if (CollectionUtils.isEmpty(codeList)) {
            return null;
        } else if (codeList.size() > 1) {
            for (ThunderCode meta : codeList) {
                thunderCodeDao.deleteById(meta.getId());
            }
            return null;
        }
        return codeList.get(0);
    }

    public List<ThunderCode> findLeftCodeByType(Integer type) {
        ThunderCodeVO vo = new ThunderCodeVO();
        vo.setType(type);
        vo.setIsUse(false);
        List<ThunderCode> codeList = thunderCodeDao.findByParameter(vo);
        return codeList;
    }

    @Override
    public ThunderCodeVO getTwoHoursWelfare() {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }

        //判断用户是否已领取奖励
        if (redisOpenService.getBitSet(RedisKeyEnum.THUNDER_WELFARE_2_HOURS.generateKey(), user.getId())) {
            throw new ThunderCodeException(ThunderCodeException.ExceptionCode.WELFARE_USED);
        }

        //判断奖励是否已领完
        List<ThunderCode> sohuCodeList = findLeftCodeByType(ThunderCodeEnum.SOHU.getType());
        List<ThunderCode> ximaCodeList = findLeftCodeByType(ThunderCodeEnum.XIMALAYA.getType());
        if (CollectionUtils.isEmpty(sohuCodeList) || CollectionUtils.isEmpty(ximaCodeList)) {
            throw new ThunderCodeException(ThunderCodeException.ExceptionCode.NO_WELFARE_LEFT);
        }

        OrderVO orderVO = orderService.getThunderOrderInfo();
        if (orderVO.getSumOrderHours().compareTo(new BigDecimal(3)) > 0) {
            ThunderCode sohuCode = sohuCodeList.get(0);
            ThunderCode ximaCode = ximaCodeList.get(0);
            sohuCode.setUserId(user.getId());
            sohuCode.setIsUse(true);
            sohuCode.setUpdateTime(DateUtil.date());
            thunderCodeDao.update(sohuCode);

            ximaCode.setUserId(user.getId());
            ximaCode.setIsUse(true);
            ximaCode.setUpdateTime(DateUtil.date());
            thunderCodeDao.update(ximaCode);
            redisOpenService.bitSet(RedisKeyEnum.THUNDER_WELFARE_2_HOURS.generateKey(), user.getId());

            ThunderCodeVO resultVO = new ThunderCodeVO();
            resultVO.setSohuCode(sohuCode.getCode());
            resultVO.setXimaCode(ximaCode.getCode());
            return resultVO;
        }
        return null;
    }

    @Override
    public ThunderCodeVO getOneOrderWelfare() {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }

        //判断用户是否已领取奖励
        if (redisOpenService.getBitSet(RedisKeyEnum.THUNDER_WELFARE_1_ORDER.generateKey(), user.getId())) {
            throw new ThunderCodeException(ThunderCodeException.ExceptionCode.WELFARE_USED);
        }

        //判断奖励是否已领完
        List<ThunderCode> steamSdkList = findLeftCodeByType(ThunderCodeEnum.STEAM_SDK.getType());
        if (CollectionUtils.isEmpty(steamSdkList)) {
            throw new ThunderCodeException(ThunderCodeException.ExceptionCode.NO_WELFARE_LEFT);
        }

        OrderVO orderVO = orderService.getThunderOrderInfo();
        if (orderVO.getOrderCount() > 0) {
            //领取奖励
            ThunderCode steamSdk = steamSdkList.get(0);
            steamSdk.setUserId(user.getId());
            steamSdk.setIsUse(true);
            steamSdk.setUpdateTime(DateUtil.date());
            thunderCodeDao.update(steamSdk);

            redisOpenService.bitSet(RedisKeyEnum.THUNDER_WELFARE_1_ORDER.generateKey(), user.getId());
            ThunderCodeVO resultVO = new ThunderCodeVO();
            resultVO.setSteamSdk(steamSdk.getCode());
            return resultVO;
        }
        return null;
    }

    @Override
    public ThunderCodeVO getThreeOrderWelfare() {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }

        //判断用户是否已领取奖励
        if (redisOpenService.getBitSet(RedisKeyEnum.THUNDER_WELFARE_3_ORDER.generateKey(), user.getId())) {
            throw new ThunderCodeException(ThunderCodeException.ExceptionCode.WELFARE_USED);
        }

        //判断奖励是否已领完
        List<ThunderCode> steamAccountList = findLeftCodeByType(ThunderCodeEnum.STEAM_ACCOUNT_PSW.getType());
        List<ThunderCode> cibnCodeList = findLeftCodeByType(ThunderCodeEnum.CIBN.getType());
        if (CollectionUtils.isEmpty(steamAccountList) || CollectionUtils.isEmpty(cibnCodeList)) {
            throw new ThunderCodeException(ThunderCodeException.ExceptionCode.NO_WELFARE_LEFT);
        }

        OrderVO orderVO = orderService.getThunderOrderInfo();
        if (orderVO.getOrderCount() >= 3) {
            ThunderCode steamAccount = steamAccountList.get(0);
            ThunderCode cibnCode = cibnCodeList.get(0);
            steamAccount.setUserId(user.getId());
            steamAccount.setIsUse(true);
            steamAccount.setUpdateTime(DateUtil.date());
            thunderCodeDao.update(steamAccount);

            cibnCode.setUserId(user.getId());
            cibnCode.setIsUse(true);
            cibnCode.setUpdateTime(DateUtil.date());
            thunderCodeDao.update(cibnCode);
            redisOpenService.bitSet(RedisKeyEnum.THUNDER_WELFARE_3_ORDER.generateKey(), user.getId());

            ThunderCodeVO resultVO = new ThunderCodeVO();
            resultVO.setSteamAccount(steamAccount.getAccount());
            resultVO.setSteamPassword(steamAccount.getPassword());
            resultVO.setCibnCode(cibnCode.getCode());
            return resultVO;
        }
        return null;
    }

    @Override
    public Coupon newUserCoupon(String ipStr) {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }

        OrderVO orderVO = orderService.getThunderOrderInfo();
        if (orderVO.getOrderCount() == 0) {
            Coupon coupon = couponOpenService.generateCoupon(Constant.THUNDER_COUPON_REDEEM_CODE, user.getId(), DateUtil.date(), ipStr);
            return coupon;
        }
        return null;
    }
}
