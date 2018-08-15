package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.FenqileReconRecordVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.FenqileReconRecordDao;
import com.fulu.game.core.entity.FenqileReconRecord;
import com.fulu.game.core.service.FenqileReconRecordService;

import java.util.Date;
import java.util.List;


@Service
public class FenqileReconRecordServiceImpl extends AbsCommonService<FenqileReconRecord,Integer> implements FenqileReconRecordService {

    private final FenqileReconRecordDao fenqileReconRecordDao;

    @Autowired
    public FenqileReconRecordServiceImpl(FenqileReconRecordDao fenqileReconRecordDao) {
        this.fenqileReconRecordDao = fenqileReconRecordDao;
    }


    @Override
    public ICommonDao<FenqileReconRecord, Integer> getDao() {
        return fenqileReconRecordDao;
    }

    public PageInfo<FenqileReconRecord> reconRecord(Integer pageNum, Integer pageSize, Date startTime, Date endTime) {
        String orderBy = "id DESC";

        FenqileReconRecordVO vo = new FenqileReconRecordVO();
        vo.setProcessStartTime(startTime);
        vo.setProcessEndTime(endTime);
        List<FenqileReconRecord> list = fenqileReconRecordDao.findReconRecordByTime(vo);

        PageHelper.startPage(pageNum, pageSize, orderBy);
        return new PageInfo<>(list);
    }
}
