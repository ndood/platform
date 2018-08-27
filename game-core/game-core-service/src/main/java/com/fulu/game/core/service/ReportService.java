package com.fulu.game.core.service;

import com.fulu.game.core.entity.Report;
import com.fulu.game.core.entity.vo.ReportVO;
import com.github.pagehelper.PageInfo;


/**
 * 举报表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-27 13:55:55
 */
public interface ReportService extends ICommonService<Report, Integer> {

    void submit(ReportVO reportVO);

    PageInfo<ReportVO> list(ReportVO reportVO);

    boolean remark(Integer id, String remark);

    boolean process(Integer id);
}
