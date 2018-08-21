package com.fulu.game.admin.service;

import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.service.UserInfoAuthService;

public interface AdminUserInfoAuthService extends UserInfoAuthService {

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
}
