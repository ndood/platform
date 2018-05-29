package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.entity.vo.UserInfoVO;
import com.github.pagehelper.PageInfo;

/**
 * 信息认证表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-20 11:12:13
 */
public interface UserInfoAuthService extends ICommonService<UserInfoAuth,Integer>{

    UserInfoAuthVO save(UserInfoAuthVO userInfoAuthVO);

    /**
     * 通过用户ID查询用户认证信息
      * @param userId
     * @return
     */
    UserInfoAuth findByUserId(Integer userId);
    /**
     * 认证信息驳回
     * @param id
     * @return
     */
    UserInfoAuth reject(Integer id,String reason);

    /**
     * 清楚驳回标记
     * @param id
     * @return
     */
    UserInfoAuth unReject(Integer id);

    /**
     * 冻结用户认证信息
     * @param id
     * @param reason
     * @return
     */
    UserInfoAuth freeze(Integer id,String reason);

    UserInfoAuth unFreeze(Integer id);

    /**
     * 查找用户个人认证信息
     * @param userId
     * @return
     */
    UserInfoAuthVO findUserAuthInfoByUserId(Integer userId);

    /**
     * 个人信息认证列表
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    PageInfo<UserInfoAuthVO> list(Integer pageNum, Integer pageSize, String orderBy,String mobile,String startTime,String endTime);

    /**
     * 查询用户名片
     * @param userId
     * @return
     */
    UserInfoVO findUserCardByUserId(Integer userId,Boolean hasPhotos,Boolean hasVoice,Boolean hasTags,Boolean hasTechs);

    /**
     * 查询用户技能分享名片信息
     * @return
     */
    UserInfoVO findUserTechCardByUserId(Integer userId,Integer techAuthId);

    /**
     * 查询用户技能分享名片信息
     * @return
     */
    UserInfoVO getSharePage(Integer techAuthId);

}
