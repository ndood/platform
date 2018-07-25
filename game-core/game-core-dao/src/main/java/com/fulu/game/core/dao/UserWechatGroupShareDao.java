package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserWechatGroupShare;
import com.fulu.game.core.entity.vo.UserWechatGroupShareVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户微信群分享表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-07-25 10:41:56
 */
@Mapper
public interface UserWechatGroupShareDao extends ICommonDao<UserWechatGroupShare, Integer> {

    List<UserWechatGroupShare> findByParameter(UserWechatGroupShareVO userWechatGroupShareVO);

    /**
     * 根据用户id获取用户微信群分享信息
     *
     * @param userId 用户id
     * @return 用户微信群分享信息
     */
    UserWechatGroupShareVO findByUserId(Integer userId);

}
