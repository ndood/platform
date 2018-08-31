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
     * 根据条件获取陪玩师信息
     *
     * @param userInfoAuthSearchVO
     * @return
     */
    public List<UserInfoAuthVO> findBySearchVO(UserInfoAuthSearchVO userInfoAuthSearchVO);

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
     * 设置客服代聊ID
     *
     * @param id 客服ID
     */
    void setSubstitute(int id, Integer substituteId);

    boolean modifyCharm(Integer userId, Integer price);

}
