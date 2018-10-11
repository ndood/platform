package com.fulu.game.core.service;

import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.UserTechInfo;
import com.fulu.game.core.entity.to.UserTechAuthTO;
import com.fulu.game.core.entity.vo.TechProductOrderVO;
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
     * 添加和修改技能认证信息
     * @param userTechAuthTO
     * @return
     */
    UserTechAuthTO save(UserTechAuthTO userTechAuthTO);


    /**
     * 设置
     * @param techId
     */
    void settingsTechMain(int techId);



    /**
     * 用户技能认证信息(包括标签和段位)
     * @param id
     * @return
     */
    UserTechAuthVO findTechAuthVOById(Integer id,Integer categoryId);

    /**
     * 查询用户正常可用的技能
     * @param userId
     * @return
     */
    List<UserTechAuth> findUserNormalTechs(Integer userId);

    /**
     * 查询用户认证技能的分类
     * @param userId
     * @return
     */
    List<Integer> findUserNormalCategoryIds(Integer userId);

    /**
     * 查询用户段位信息
     * @param techAuthId
     * @return
     */
    UserTechInfo findDanInfo(Integer techAuthId);

    /**
     * 查询用户所有技能
     * @param userId
     * @return
     */
    List<UserTechAuth> findByUserId(int userId);

    /**
     * 检查用户技能认证状态
     * @param techAuthId
     */
    void checkUserTechAuth(Integer techAuthId);

    /**
     * 查询游戏分类通过的技能认证
     * @param categoryId
     * @return
     */
    List<UserTechAuth> findNormalByCategory(int categoryId);

    /**
     * 通过游戏分类和用户ID查询用户技能
     * @param categoryId
     * @param userId
     * @return
     */
    List<UserTechAuth> findByCategoryAndUser(Integer categoryId, Integer userId);

    /**
     * 通过游戏分类和用户ID查询用户技能
     * @param categoryId
     * @param userId
     * @return
     */
    UserTechAuth findTechByCategoryAndUser(Integer categoryId, Integer userId);

    /**
     * 通过用户ID查询技能认证
     * @param userId
     * @return
     */
    List<UserTechAuth> findByStatusAndUserId(int userId, Integer status);


    void updateByCategory(Category category);


    PageInfo<UserTechAuthVO> list(Integer pageNum, Integer pageSize, UserTechAuthSearchVO userTechAuthSearchVO);

    /**
     * 查询陪玩师所有可下单的商品
     * @param productId
     * @return
     */
    TechProductOrderVO getTechProductByProductId(Integer productId);

    /**
     * 查询激活用户技能列表
     * @param userId
     * @return
     */
    List<UserTechAuth> findUserActivateTechs(Integer userId);

}
