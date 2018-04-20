package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserInfoFile;

import java.util.List;
import java.util.Map;

/**
 * 用户信息文件表(图片、声音)
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-20 11:12:13
 */
public interface UserInfoFileService extends ICommonService<UserInfoFile,Integer>{



    List<UserInfoFile> findByUserId(Integer userId);
	
}
