package com.fulu.game.core.dao;

import com.fulu.game.core.entity.DynamicFile;
import com.fulu.game.core.entity.vo.DynamicFileVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 动态文件表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-08-30 11:02:13
 */
@Mapper
public interface DynamicFileDao extends ICommonDao<DynamicFile,Long>{

    List<DynamicFile> findByParameter(DynamicFileVO dynamicFileVO);

}
