package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserInfoAuthFileTemp;



/**
 * 信息认证文件临时表（图片、声音）
 * 
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-07-19 20:14:50
 */
public interface UserInfoAuthFileTempService extends ICommonService<UserInfoAuthFileTemp,Integer>{

    /**
     * 根据userId删除表记录
     * @param userId
     * @return
     */
    Integer deleteByUserId(Integer userId);
}
