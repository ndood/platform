package com.fulu.game.core.dao;

import com.fulu.game.core.entity.FenqileReconRecord;
import com.fulu.game.core.entity.vo.FenqileReconRecordVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 分期乐对账记录表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-15 20:26:58
 */
@Mapper
public interface FenqileReconRecordDao extends ICommonDao<FenqileReconRecord, Integer> {

    List<FenqileReconRecord> findByParameter(FenqileReconRecordVO fenqileReconRecordVO);

    List<FenqileReconRecord> findReconRecordByTime(FenqileReconRecordVO vo);
}
