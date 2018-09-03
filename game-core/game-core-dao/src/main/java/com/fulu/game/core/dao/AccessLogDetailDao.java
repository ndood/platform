package com.fulu.game.core.dao;

import com.fulu.game.core.entity.AccessLogDetail;
import com.fulu.game.core.entity.vo.AccessLogDetailVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 访问日志详情表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-08-30 11:27:41
 */
@Mapper
public interface AccessLogDetailDao extends ICommonDao<AccessLogDetail,Long>{

    List<AccessLogDetail> findByParameter(AccessLogDetailVO accessLogDetailVO);

}
