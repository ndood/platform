package com.fulu.game.core.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.fulu.game.core.dao.FenqileReconRecordDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.FenqileReconRecord;
import com.fulu.game.core.entity.vo.FenqileReconRecordVO;
import com.fulu.game.core.service.FenqileReconRecordService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class FenqileReconRecordServiceImpl extends AbsCommonService<FenqileReconRecord, Integer> implements FenqileReconRecordService {

    private final FenqileReconRecordDao fenqileReconRecordDao;

    @Autowired
    public FenqileReconRecordServiceImpl(FenqileReconRecordDao fenqileReconRecordDao) {
        this.fenqileReconRecordDao = fenqileReconRecordDao;
    }


    @Override
    public ICommonDao<FenqileReconRecord, Integer> getDao() {
        return fenqileReconRecordDao;
    }

    @Override
    public PageInfo<FenqileReconRecordVO> reconRecord(Integer pageNum, Integer pageSize, Date startTime, Date endTime) {
        String orderBy = "id DESC";

        FenqileReconRecordVO vo = new FenqileReconRecordVO();
        vo.setProcessStartTime(startTime);
        vo.setProcessEndTime(endTime);

        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<FenqileReconRecord> list = fenqileReconRecordDao.findReconRecordByTime(vo);
        List<FenqileReconRecordVO> voList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            for (FenqileReconRecord meta : list) {
                FenqileReconRecordVO recordVO = new FenqileReconRecordVO();
                BeanUtil.copyProperties(meta, recordVO);

                Date orderCompleteTime = meta.getOrderCompleteTime();
                if (orderCompleteTime != null) {
                    recordVO.setOrderInterval(DateUtil.formatDateTime(orderCompleteTime));
                } else {
                    recordVO.setOrderInterval(DateUtil.formatDateTime(meta.getStartTime()) + " ~ "
                            + DateUtil.formatDateTime(meta.getEndTime()));
                }
                voList.add(recordVO);
            }
        }

        PageInfo page = new PageInfo(list);
        page.setList(voList);
        return page;
    }
}
