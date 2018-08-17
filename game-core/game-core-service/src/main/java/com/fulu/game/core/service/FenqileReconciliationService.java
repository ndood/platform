package com.fulu.game.core.service;

import com.fulu.game.core.entity.FenqileReconciliation;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 分期乐对账表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-14 18:24:00
 */
public interface FenqileReconciliationService extends ICommonService<FenqileReconciliation, Integer> {

    FenqileReconciliation findByOrderNo(String orderNo);

    void recon(String orderNos, Date startTime, Date endTime, String remark,
               Integer unReconCount, BigDecimal unReconTotalAmount);
}
