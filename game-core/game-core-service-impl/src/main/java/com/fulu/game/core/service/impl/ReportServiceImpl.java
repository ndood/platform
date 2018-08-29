package com.fulu.game.core.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.ReportDao;
import com.fulu.game.core.dao.ReportFileDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.Report;
import com.fulu.game.core.entity.ReportFile;
import com.fulu.game.core.entity.vo.ReportFileVO;
import com.fulu.game.core.entity.vo.ReportVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.ReportService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class ReportServiceImpl extends AbsCommonService<Report, Integer> implements ReportService {

    private final ReportDao reportDao;
    private final ReportFileDao reportFileDao;
    private final OssUtil ossUtil;
    private final AdminService adminService;

    @Autowired
    public ReportServiceImpl(ReportDao reportDao,
                             ReportFileDao reportFileDao,
                             OssUtil ossUtil,
                             AdminService adminService) {
        this.reportDao = reportDao;
        this.reportFileDao = reportFileDao;
        this.ossUtil = ossUtil;
        this.adminService = adminService;
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
        report.setUpdateTime(DateUtil.date());
        report.setCreateTime(DateUtil.date());
        reportDao.create(report);

        if (!Arrays.isNullOrEmpty(fileUrl)) {
            for (String url : fileUrl) {
                ReportFile file = new ReportFile();
                file.setReportId(report.getId());
                file.setUrl(ossUtil.activateOssFile(url));
                file.setUpdateTime(DateUtil.date());
                file.setCreateTime(DateUtil.date());
                reportFileDao.create(file);
            }
        }
    }

    @Override
    public PageInfo<ReportVO> list(ReportVO reportVO) {
        String orderBy = reportVO.getOrderBy();
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "report.create_time DESC";
        }

        PageHelper.startPage(reportVO.getPageNum(), reportVO.getPageSize(), orderBy);
        List<ReportVO> reportVOList = reportDao.list(reportVO);
        if (CollectionUtil.isEmpty(reportVOList)) {
            return null;
        }

        for (ReportVO vo : reportVOList) {
            Integer reportId = vo.getId();

            ReportFileVO reportFileVO = new ReportFileVO();
            reportFileVO.setReportId(reportId);
            List<ReportFile> fileList = reportFileDao.findByParameter(reportFileVO);
            if (CollectionUtil.isEmpty(fileList)) {
                continue;
            }
            List<String> fileUrlList = new ArrayList<>();
            for (ReportFile file : fileList) {
                fileUrlList.add(file.getUrl());
            }
            vo.setFileUrl(fileUrlList.toArray(new String[0]));
        }

        return new PageInfo<>(reportVOList);
    }

    @Override
    public boolean remark(Integer id, String remark) {
        Report report = new Report();
        report.setId(id);
        report.setRemark(remark);
        report.setUpdateTime(DateUtil.date());
        int flag = reportDao.update(report);
        return flag > 0;
    }

    @Override
    public boolean process(Integer id) {
        Admin admin = adminService.getCurrentUser();

        Report report = new Report();
        report.setId(id);
        report.setStatus(Constant.IS_PROCESSED);
        report.setProcessTime(DateUtil.date());
        report.setAdminId(admin.getId());
        report.setAdminName(admin.getName());
        report.setUpdateTime(DateUtil.date());
        int flag = reportDao.update(report);
        return flag > 0;
    }
}
