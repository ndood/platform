package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserInfoAuthFileTemp;
import com.fulu.game.core.entity.vo.UserInfoAuthFileTempVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 信息认证文件临时表（图片、声音）
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-07-19 20:14:50
 */
@Mapper
public interface UserInfoAuthFileTempDao extends ICommonDao<UserInfoAuthFileTemp,Integer>{

    List<UserInfoAuthFileTemp> findByParameter(UserInfoAuthFileTempVO userInfoAuthFileTempVO);

    Integer deleteByUserId(Integer userId);

}
