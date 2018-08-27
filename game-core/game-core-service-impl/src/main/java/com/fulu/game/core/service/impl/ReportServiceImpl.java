package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.ReportDao;
import com.fulu.game.core.dao.ReportFileDao;
import com.fulu.game.core.entity.Report;
import com.fulu.game.core.entity.ReportFile;
import com.fulu.game.core.entity.vo.ReportVO;
import com.fulu.game.core.service.ReportService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class ReportServiceImpl extends AbsCommonService<Report, Integer> implements ReportService {

    private final ReportDao reportDao;
    private final ReportFileDao reportFileDao;
    private final OssUtil ossUtil;

    @Autowired
    public ReportServiceImpl(ReportDao reportDao, ReportFileDao reportFileDao, OssUtil ossUtil) {
        this.reportDao = reportDao;
        this.reportFileDao = reportFileDao;
        this.ossUtil = ossUtil;
    }


    @Override
    public ICommonDao<Report, Integer> getDao() {
        return reportDao;
    }

    @Override
    public void submit(ReportVO reportVO) {
        Integer userId = reportVO.getUserId();
        Integer reportedUserId = reportVO.getReportedUserId();
        String content = reportVO.getContent();
        String[] fileUrl = reportVO.getFileUrl();

        boolean paramFlag = (userId == null) || (reportedUserId == null) || StringUtils.isBlank(content);
        if (paramFlag) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }

        Report report = new Report();
        report.setUserId(userId);
        report.setReportedUserId(reportedUserId);
        report.setContent(content);
        report.setStatus(Constant.UN_PROCESSED);
        report.setProcessTime(DateUtil.date());
        report.setUpdateTime(DateUtil.date());
        report.setCreateTime(DateUtil.date());
        reportDao.create(report);

        if (!Arrays.isNullOrEmpty(fileUrl)) {
            for (String url : fileUrl) {
                ReportFile file = new ReportFile();
                file.setReportId(report.getId());
                //fixme
//                file.setUrl(ossUtil.activateOssFile(url));
                file.setUrl(url);
                file.setUpdateTime(DateUtil.date());
                file.setCreateTime(DateUtil.date());
                reportFileDao.create(file);
            }
        }
    }

    @Override
    public PageInfo<ReportVO> list(Integer status, Date startTime, Date endTime) {
        return null;
    }
}
