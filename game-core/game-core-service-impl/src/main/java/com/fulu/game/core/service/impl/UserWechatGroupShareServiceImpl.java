package com.fulu.game.core.service.impl;


import cn.binarywang.wx.miniapp.util.crypt.WxMaCryptUtils;
import cn.hutool.json.JSONObject;
import com.fulu.game.common.Constant;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserWechatGroupShareDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.UserWechatGroupShareVO;
import com.fulu.game.core.service.CouponGroupService;
import com.fulu.game.core.service.CouponService;
import com.fulu.game.core.service.RegistSourceService;
import com.fulu.game.core.service.UserWechatGroupShareService;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class UserWechatGroupShareServiceImpl extends AbsCommonService<UserWechatGroupShare, Integer>
        implements UserWechatGroupShareService {

    private final UserWechatGroupShareDao userWechatGroupShareDao;
    private final RegistSourceService registSourceService;
    private final CouponService couponService;
    private final CouponGroupService couponGroupService;

    @Autowired
    public UserWechatGroupShareServiceImpl(UserWechatGroupShareDao userWechatGroupShareDao,
                                           RegistSourceService registSourceService,
                                           CouponService couponService, CouponGroupService couponGroupService) {
        this.userWechatGroupShareDao = userWechatGroupShareDao;
        this.registSourceService = registSourceService;
        this.couponService = couponService;
        this.couponGroupService = couponGroupService;
    }

    @Override
    public ICommonDao<UserWechatGroupShare, Integer> getDao() {
        return userWechatGroupShareDao;
    }

    @Override
    public UserWechatGroupShareVO getUserShareStatus(User user) {
        if (user == null) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        Integer userId = user.getId();
        if (userId == null) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        UserWechatGroupShareVO groupShareVO = userWechatGroupShareDao.findByUserId(userId);
        RegistSource registSource = registSourceService.findCjRegistSource();
        Integer sourceId = Constant.CJ_SOURCE_ID;
        if (registSource != null) {
            sourceId = registSource.getId();
        }

        //查询优惠券总量 如果总量为0（在发放中途被人为修改干预） 表示优惠券通道关闭
        CouponGroup couponGroup = couponGroupService.findByRedeemCode(Constant.CJ_COUPON_GROUP_REDEEM_CODE);
        Integer amount = 0;
        if (couponGroup != null) {
            amount = couponGroup.getAmount();
        }

        if (groupShareVO != null) {
            groupShareVO.setSourceId(sourceId);
            if (amount > 0) {
                groupShareVO.setIsCouponAvailable(Constant.COUPON_AVAILABLE);
            } else {
                groupShareVO.setIsCouponAvailable(Constant.COUPON_UNAVAILABLE);
            }
            return groupShareVO;
        }

        UserWechatGroupShareVO resultGroupShareVO = new UserWechatGroupShareVO();
        resultGroupShareVO.setUserId(userId);
        resultGroupShareVO.setShareStatus(Constant.WECHAT_GROUP_SHARE_NOT_FINISHED);
        resultGroupShareVO.setGroupCounts(0);
        resultGroupShareVO.setSourceId(sourceId);
        resultGroupShareVO.setCoupouStatus(Constant.SEND_COUPOU_FAIL);

        UserWechatGroupShare paramGroupShare = new UserWechatGroupShare();
        BeanUtil.copyProperties(resultGroupShareVO, paramGroupShare);
        paramGroupShare.setUpdateTime(DateUtil.date());
        paramGroupShare.setCreateTime(DateUtil.date());
        userWechatGroupShareDao.create(paramGroupShare);

        if (amount > 0) {
            resultGroupShareVO.setIsCouponAvailable(Constant.COUPON_AVAILABLE);
        } else {
            resultGroupShareVO.setIsCouponAvailable(Constant.COUPON_UNAVAILABLE);
        }

        return resultGroupShareVO;
    }

    @Override
    public boolean shareWechatGroup(User user, String sessionKey, String encryptedData, String iv, String ipStr) {
        if (user == null) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }

        Integer userId = user.getId();
        if (userId == null) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        String resultStr = WxMaCryptUtils.decrypt(sessionKey, encryptedData, iv);
        JSONObject jo = new JSONObject(resultStr);
        String openGId = jo.getStr("openGId");
        log.info("用户userId:{} 将信息分享到微信群:{}", userId, openGId);

        UserWechatGroupShareVO groupShareVO = userWechatGroupShareDao.findByUserId(userId);
        if (groupShareVO == null) {
            log.info("插入用户userId:{}的第一条微信群分享记录！", userId);
            UserWechatGroupShare groupShare = new UserWechatGroupShare();
            groupShare.setUserId(userId);
            groupShare.setShareStatus(Constant.WECHAT_GROUP_SHARE_NOT_FINISHED);
            groupShare.setGroupCounts(1);
            groupShare.setGroupIds(openGId);
            groupShare.setUpdateTime(DateUtil.date());
            groupShare.setCreateTime(DateUtil.date());
            create(groupShare);
            return true;
        }

        String groupIds = groupShareVO.getGroupIds();
        if (StringUtils.isBlank(groupIds)) {
            UserWechatGroupShare groupShare = new UserWechatGroupShare();
            groupShare.setId(groupShareVO.getId());
            groupShare.setUserId(userId);
            groupShare.setGroupCounts(groupShareVO.getGroupCounts() + 1);
            groupShare.setGroupIds(openGId);
            update(groupShare);
            return true;
        }

        if (groupIds.contains(Constant.DEFAULT_SPLIT_SEPARATOR)) {
            String[] groupIdsArray = groupIds.split(Constant.DEFAULT_SPLIT_SEPARATOR);
            boolean alreadyFinishShare = groupIdsArray.length >= Constant.WECHAT_GROUP_SHARE_MAXIMUM
                    || groupShareVO.getShareStatus().equals(Constant.WECHAT_GROUP_SHARE_FINISHED);
            if (alreadyFinishShare) {
                //已完成分享任务
                return false;
            }
            for (String meta : groupIdsArray) {
                if (meta.equals(openGId)) {
                    //分享到重复群
                    return false;
                }
            }

            if (groupIdsArray.length == Constant.WECHAT_GROUP_SHARE_MAXIMUM - 1) {
                UserWechatGroupShare groupShare = new UserWechatGroupShare();
                groupShare.setId(groupShareVO.getId());
                groupShare.setUserId(userId);
                groupShare.setShareStatus(Constant.WECHAT_GROUP_SHARE_FINISHED);
                groupShare.setGroupCounts(groupShareVO.getGroupCounts() + 1);
                groupShare.setGroupIds(groupShareVO.getGroupIds() + Constant.DEFAULT_SPLIT_SEPARATOR + openGId);
                update(groupShare);

                CouponGroup couponGroup = couponGroupService.findByRedeemCode(Constant.CJ_COUPON_GROUP_REDEEM_CODE);
                if (couponGroup == null) {
                    throw new ServiceErrorException("查询不到CJ活动的优惠券！");
                }
                Coupon coupon = couponService.generateCoupon(couponGroup.getRedeemCode(), userId, DateUtil.date(), ipStr);
                if (coupon == null) {
                    throw new ServiceErrorException("通过优惠券兑换码发放优惠券失败！");
                }
                updateCoupouStatus(userId);
                return true;
            } else {
                updateUserWechatGroupShare(userId, openGId, groupShareVO);
                return true;
            }
        } else {
            if (groupIds.equals(openGId)) {
                return false;
            }
            updateUserWechatGroupShare(userId, openGId, groupShareVO);
            return true;
        }
    }

    /**
     * 更新用户分享信息
     *
     * @param userId       用户id
     * @param openGId      群对当前小程序的唯一 ID
     * @param groupShareVO 用户分享信息VO
     */
    private void updateUserWechatGroupShare(Integer userId, String openGId, UserWechatGroupShareVO groupShareVO) {
        UserWechatGroupShare groupShare = new UserWechatGroupShare();
        groupShare.setId(groupShareVO.getId());
        groupShare.setUserId(userId);
        groupShare.setGroupCounts(groupShareVO.getGroupCounts() + 1);
        groupShare.setGroupIds(groupShareVO.getGroupIds() + Constant.DEFAULT_SPLIT_SEPARATOR + openGId);
        update(groupShare);
    }

    /**
     * 修改用户对应的优惠券发放状态
     *
     * @param userId 用户id
     */
    private void updateCoupouStatus(Integer userId) {
        UserWechatGroupShareVO paramVO = userWechatGroupShareDao.findByUserId(userId);
        UserWechatGroupShare groupShare = new UserWechatGroupShare();
        BeanUtil.copyProperties(paramVO, groupShare);
        groupShare.setCoupouStatus(Constant.SEND_COUPOU_SUCCESS);
        userWechatGroupShareDao.update(groupShare);
    }
}
