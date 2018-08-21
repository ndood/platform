package com.fulu.game.admin.service;

import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.service.UserTechAuthService;

public interface AdminUserTechAuthService extends UserTechAuthService {



    /**
     * 驳回用户技能认证
     * @param id
     * @param reason
     * @return
     */
    UserTechAuth reject(Integer id, String reason);

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

}
