package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserInfoFile;
import com.fulu.game.core.entity.vo.UserInfoFileVO;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户信息文件表(图片、声音)
 * @author wangbin
 * @email ${email}
 * @date 2018-04-20 11:12:13
 */
@Mapper
public interface UserInfoFileDao extends ICommonDao<UserInfoFile,Integer>{

    List<UserInfoFile> findByParameter(UserInfoFileVO userInfoFileVO);

}
