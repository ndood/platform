package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserInfoAuthFile;

import java.util.List;
import java.util.Map;

/**
 * 信息认证文件表（图片、声音）
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-20 11:12:13
 */
public interface UserInfoAuthFileService extends ICommonService<UserInfoAuthFile,Integer>{


     List<UserInfoAuthFile> findByUserAuthIdAndType(Integer userAuthId, Integer fileType);

     void deleteByUserAuthIdAndType(Integer userAuthId, Integer fileType);


}
