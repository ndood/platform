package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.UserTechInfo;
import com.fulu.game.core.entity.vo.UserTechAuthVO;
import com.fulu.game.core.entity.vo.searchVO.UserTechAuthSearchVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 技能认证表
 *
 * @author wangbin
 * @email ${email}
 * @date 2018-04-23 11:17:40
 */
public interface UserTechAuthService extends ICommonService<UserTechAuth, Integer> {

    /**
     * 保存用户技能
     * @param userTechAuthVO
     * @return
     */
    UserTechAuthVO save(UserTechAuthVO userTechAuthVO);

    /**
     * 驳回用户技能认证
     * @param id
     * @param reason
     * @return
     */
    UserTechAuth reject(Integer id,String reason);

    /**
     * 技能审核通过
     * @param id
     * @return
     */
    UserTechAuth pass(Integer id);

    /**
     * 冻结用户技能认证
     * @param id
     * @param reason
     * @return
     */
    UserTechAuth freeze(Integer id,String reason);

    /**
     * 解冻用户技能认证
     * @param id
     * @return
     */
    UserTechAuth unFreeze(Integer id);

    /**
     * 用户技能认证信息(包括标签和段位)
     * @param id
     * @return
     */
    UserTechAuthVO findTechAuthVOById(Integer id);

    /**
     * 查询用户正常可用的技能
     * @param userId
     * @return
     */
    List<UserTechAuth> findUserNormalTechs(Integer userId);

    /**
     * 查询用户段位信息
     * @param techAuthId
     * @return
     */
    UserTechInfo findDanInfo(Integer techAuthId);


    List<UserTechAuth> findByUserId(Integer userId);


    void checkUserTechAuth(Integer techAuthId);

    /**
     * 通过用户ID查询技能认证
     * @param userId
     * @return
     */
    List<UserTechAuth> findByStatusAndUserId(Integer userId, Integer status);


    PageInfo<UserTechAuthVO> list(Integer pageNum, Integer pageSize, String orderBy, UserTechAuthSearchVO userTechAuthSearchVO);

}
