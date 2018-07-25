package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.to.UserInfoAuthTO;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.entity.vo.UserInfoVO;
import com.fulu.game.core.entity.vo.searchVO.UserInfoAuthSearchVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 信息认证表
 *
 * @author wangbin
 * @email ${email}
 * @date 2018-04-20 11:12:13
 */
public interface UserInfoAuthService extends ICommonService<UserInfoAuth, Integer> {


    /**
     * 保存用户认证个人信息
     *
     * @param userInfoAuthTO
     * @return
     */
    UserInfoAuth save(UserInfoAuthTO userInfoAuthTO);

    /**
     * 通过用户ID查询用户认证信息
     *
     * @param userId
     * @return
     */
    UserInfoAuth findByUserId(int userId);

    /**
     * 认证信息驳回
     *
     * @param id
     * @return
     */
    UserInfoAuth reject(int id, String reason);

    /**
     * 清楚驳回标记
     *
     * @param id
     * @return
     */
    UserInfoAuth pass(int id);

    /**
     * 冻结用户认证信息
     *
     * @param id
     * @param reason
     * @return
     */
    UserInfoAuth freeze(int id, String reason);

    /**
     * 解冻用户认证信息
     *
     * @param id
     * @return
     */
    UserInfoAuth unFreeze(int id);

    /**
     * 查找用户个人认证信息
     *
     * @param userId 用户id
     * @return 用户认证信息VO
     */
    UserInfoAuthVO findUserInfoAuthByUserId(Integer userId);


    List<UserInfoAuth> findByUserIds(List<Integer> userIds);

    /**
     * 个人信息认证列表
     *
     * @param pageNum              页码
     * @param pageSize             每页显示数据条数
     * @param userInfoAuthSearchVO 查询条件VO
     * @return 分页数据结果
     */
    PageInfo<UserInfoAuthVO> list(Integer pageNum, Integer pageSize, UserInfoAuthSearchVO userInfoAuthSearchVO);

    /**
     * 给陪玩师增加外链来源
     *
     * @param userId   陪玩师用户id
     * @param sourceId 来源id
     * @return 是否操作成功
     */
    boolean addSource(Integer userId, Integer sourceId);

    /**
     * 设置陪玩师是否在平台内展示
     *
     * @param userId   陪玩师用户id
     * @param showFlag 是否展示（0：否，1：是）
     * @return 是否操作成功
     */
    boolean isPlatformShow(Integer userId, Integer showFlag);

    /**
     * 根据userId更新用户认证信息
     *
     * @param userInfoAuth 用户认证信息bean
     * @return 是否操作成功
     */
    boolean updateByUserId(UserInfoAuth userInfoAuth);

    /**
     * 查询用户名片
     *
     * @param userId
     * @return
     */
    UserInfoVO findUserCardByUserId(int userId, Boolean hasPhotos, Boolean hasVoice, Boolean hasTags, Boolean hasTechs);

    /**
     * 查询用户技能分享名片信息
     *
     * @return
     */
    UserInfoVO findUserTechCardByUserId(int techAuthId);

    /**
     * 查询用户技能分享名片信息
     *
     * @return
     */
    UserInfoVO getSharePage(int techAuthId);


    /**
     * 设置陪玩师推送时间间隔
     *
     * @param minute
     */
    void settingPushTimeInterval(float minute);

    /**
     * 获取所有CJ陪玩师的陪玩师认证信息列表
     *
     * @return 陪玩师认证信息列表
     */
    List<UserInfoAuth> findAllCjUsers();

    /**
     * 获取所有不在平台内展示的陪玩师的认证信息列表
     *
     * @return 陪玩师认证信息列表
     */
    List<UserInfoAuth> findPlatformNotShowUserInfoAuth();
}
