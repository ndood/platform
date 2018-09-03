package com.fulu.game.core.dao;

import com.fulu.game.core.entity.AccessLog;
import com.fulu.game.core.entity.vo.AccessLogVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 访问日志记录表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-08-30 11:24:56
 */
@Mapper
public interface AccessLogDao extends ICommonDao<AccessLog,Long>{

    List<AccessLogVO> findByParameter(AccessLogVO accessLogVO);

    /**
     * 获取来访记录
     * @param accessLogVO
     * @return
     */
    List<AccessLogVO> accessList(AccessLogVO accessLogVO);

    /**
     * 获取足迹记录
     * @param accessLogVO
     * @return
     */
    List<AccessLogVO> footprintList(AccessLogVO accessLogVO);
}
