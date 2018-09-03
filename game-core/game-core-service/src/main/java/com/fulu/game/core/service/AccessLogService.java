package com.fulu.game.core.service;

import com.fulu.game.core.entity.AccessLog;
import com.fulu.game.core.entity.vo.AccessLogVO;
import com.fulu.game.core.entity.vo.DynamicCommentVO;
import com.github.pagehelper.PageInfo;


/**
 * 访问日志记录表
 * 
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-08-30 11:24:56
 */
public interface AccessLogService extends ICommonService<AccessLog,Long>{

    /**
     * 获取来访记录
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<AccessLogVO> accessList(Integer pageNum, Integer pageSize);

    /**
     * 获取足迹记录
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<AccessLogVO> footprintList(Integer pageNum, Integer pageSize);

    /**
     * 保存访问记录信息
     * @param accessLog
     * @return
     */
    public AccessLog save(AccessLog accessLog);
}
