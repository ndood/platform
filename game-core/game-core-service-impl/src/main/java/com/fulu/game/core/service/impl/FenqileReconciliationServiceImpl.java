package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.core.dao.FenqileReconciliationDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.FenqileReconRecord;
import com.fulu.game.core.entity.FenqileReconciliation;
import com.fulu.game.core.service.FenqileReconRecordService;
import com.fulu.game.core.service.FenqileReconciliationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;


@Service
public class FenqileReconciliationServiceImpl extends AbsCommonService<FenqileReconciliation, Integer> implements FenqileReconciliationService {

    private final FenqileReconciliationDao fenqileReconciliationDao;
    private final AdminServiceImpl adminService;
    private final FenqileReconRecordService fenqileReconRecordService;

    @Autowired
    public FenqileReconciliationServiceImpl(FenqileReconciliationDao fenqileReconciliationDao,
                                            AdminServiceImpl adminService, FenqileReconRecordService fenqileReconRecordService) {
        this.fenqileReconciliationDao = fenqileReconciliationDao;
        this.adminService = adminService;
        this.fenqileReconRecordService = fenqileReconRecordService;
    }

    @Override
    public ICommonDao<FenqileReconciliation, Integer> getDao() {
        return fenqileReconciliationDao;
    }

    public void recon(String orderNos, Date startTime, Date endTime, String remark, Integer unReconCount, BigDecimal unReconTotalAmount) {
        Admin admin = adminService.getCurrentUser();

        if (orderNos.contains(Constant.DEFAULT_SPLIT_SEPARATOR)) {
            String[] orderNoList = orderNos.split(Constant.DEFAULT_SPLIT_SEPARATOR);
            for (String orderNo : orderNoList) {
                FenqileReconciliation reconciliation = fenqileReconciliationDao.findByOrderNo(orderNo);
                updateByOrderNo(reconciliation, admin, remark);

                FenqileReconRecord reconRecord = new FenqileReconRecord();
                reconRecord.setStartTime(startTime);
                reconRecord.setEndTime(endTime);
                reconRecord.setAmount(unReconTotalAmount);
                reconRecord.setOrderCount(unReconCount);
                reconRecord.setProcessTime(DateUtil.date());
                reconRecord.setAdminId(admin.getId());
                reconRecord.setAdminName(admin.getName());
                reconRecord.setRemark(remark);
                reconRecord.setUpdateTime(DateUtil.date());
                reconRecord.setCreateTime(DateUtil.date());
                fenqileReconRecordService.create(reconRecord);
            }
        } else {
            FenqileReconciliation reconciliation = fenqileReconciliationDao.findByOrderNo(orderNos);
            updateByOrderNo(reconciliation, admin, remark);

            FenqileReconRecord reconRecord = new FenqileReconRecord();
            reconRecord.setAmount(reconciliation.getAmount());
            reconRecord.setOrderCount(1);
            reconRecord.setProcessTime(DateUtil.date());
            reconRecord.setAdminId(admin.getId());
            reconRecord.setAdminName(admin.getName());
            reconRecord.setRemark(remark);
            reconRecord.setUpdateTime(DateUtil.date());
            reconRecord.setCreateTime(DateUtil.date());
            fenqileReconRecordService.create(reconRecord);
        }
    }

    private void updateByOrderNo(FenqileReconciliation reconciliation, Admin admin, String remark) {
        reconciliation.setStatus(Constant.IS_RECON);
        reconciliation.setProcessTime(DateUtil.date());
        reconciliation.setAdminId(admin.getId());
        reconciliation.setAdminName(admin.getName());
        reconciliation.setRemark(remark);
        reconciliation.setUpdateTime(DateUtil.date());
        fenqileReconciliationDao.update(reconciliation);
    }
}
