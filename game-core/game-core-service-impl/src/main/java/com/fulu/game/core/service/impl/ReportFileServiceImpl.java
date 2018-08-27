package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.ReportFileDao;
import com.fulu.game.core.entity.ReportFile;
import com.fulu.game.core.service.ReportFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ReportFileServiceImpl extends AbsCommonService<ReportFile, Integer> implements ReportFileService {

    @Autowired
    private ReportFileDao reportFileDao;


    @Override
    public ICommonDao<ReportFile, Integer> getDao() {
        return reportFileDao;
    }

}
