package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.UserTechAuthVO;

import java.util.List;

import com.fulu.game.core.entity.vo.searchVO.UserTechAuthSearchVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 技能认证表
 * @author wangbin
 * @email ${email}
 * @date 2018-04-23 11:17:40
 */
@Mapper
public interface UserTechAuthDao extends ICommonDao<UserTechAuth,Integer>{

    List<UserTechAuth> findByParameter(UserTechAuthVO userTechAuthVO);

    List<UserTechAuth> search(UserTechAuthSearchVO userTechAuthSearchVO);

    int updateByCategory(Category category);

    /**
     * 更新所有的技能不为主要的技能
     * @param userId
     * @param categoryId
     * @return
     */
    int updateTechNotMain(@Param(value = "userId") Integer userId,@Param(value = "categoryId") Integer categoryId);
}
