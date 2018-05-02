package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserInfoAuthFile;
import com.fulu.game.core.entity.vo.UserInfoAuthFileVO;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 信息认证文件表（图片、声音）
 * @author wangbin
 * @email ${email}
 * @date 2018-04-20 11:12:13
 */
@Mapper
public interface UserInfoAuthFileDao extends ICommonDao<UserInfoAuthFile,Integer>{

    List<UserInfoAuthFile> findByParameter(UserInfoAuthFileVO userInfoAuthFileVO);

    int deleteByUserAuthIdAndType(@Param(value = "infoAuthId") Integer userAuthId,@Param(value = "type")  Integer fileType);
}
