package com.fulu.game.core.service;

import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserWechatGroupShare;
import com.fulu.game.core.entity.vo.UserWechatGroupShareVO;


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
    UserWechatGroupShareVO getUserShareStatus(User user);

    /**
     * 用户分享到微信群
     *
     * @param user          用户信息
     * @param sessionKey    sessionKey
     * @param encryptedData 包括敏感数据在内的完整转发信息的加密数据
     * @param iv            加密算法的初始向量
     * @param ipStr         用户ip
     * @return 是否分享成功
     */
    boolean shareWechatGroup(User user, String sessionKey, String encryptedData, String iv, String ipStr);
}
