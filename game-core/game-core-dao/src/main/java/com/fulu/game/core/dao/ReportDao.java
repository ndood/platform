package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Report;
import com.fulu.game.core.entity.vo.ReportVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 举报表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-27 13:55:55
 */
@Mapper
public interface ReportDao extends ICommonDao<Report, Integer> {

    List<Report> findByParameter(ReportVO reportVO);

}
