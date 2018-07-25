package com.fulu.game.core.service;

import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserWechatGroupShare;


/**
 * 用户微信群分享表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-07-25 10:41:56
 */
public interface UserWechatGroupShareService extends ICommonService<UserWechatGroupShare, Integer> {

    /**
     * 根据用户信息查询用户的微信群分享状态
     *
     * @param user 用户Bean
     * @return 微信群分享状态Bean
     */
    UserWechatGroupShare getUserShareStatus(User user);
}
