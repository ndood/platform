package com.fulu.game.core.service;

import com.fulu.game.core.entity.FenqileReconRecord;
import com.github.pagehelper.PageInfo;

import java.util.Date;


/**
 * 分期乐对账记录表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-15 20:26:58
 */
public interface FenqileReconRecordService extends ICommonService<FenqileReconRecord, Integer> {
    PageInfo<FenqileReconRecord> reconRecord(Integer pageNum, Integer pageSize, Date startTime, Date endTime);
}
