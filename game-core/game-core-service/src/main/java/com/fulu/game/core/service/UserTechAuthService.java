package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.UserTechInfo;
import com.fulu.game.core.entity.vo.UserTechAuthVO;
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
     *
     * @param userTechAuthVO
     * @return
     */
    UserTechAuthVO save(UserTechAuthVO userTechAuthVO);

    /**
     * 用户技能认证信息(包括标签和段位)
     *
     * @param id
     * @return
     */
    UserTechAuthVO findTechAuthVOById(Integer id);

    /**
     * 查询用户段位信息
     * @param techAuthId
     * @return
     */
    UserTechInfo findDanInfo(Integer techAuthId);


    List<UserTechAuth> findByUserId(Integer userId);

    /**
     * 通过用户ID查询技能认证
     * @param userId
     * @return
     */
    List<UserTechAuth> findByStatusAndUserId(Integer userId, Integer status);


    PageInfo<UserTechAuthVO> list(Integer pageNum, Integer pageSize, String orderBy, UserTechAuthVO requestVo);

}
