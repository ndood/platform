package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.core.dao.FenqileReconciliationDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.FenqileReconciliation;
import com.fulu.game.core.service.FenqileReconciliationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FenqileReconciliationServiceImpl extends AbsCommonService<FenqileReconciliation, Integer> implements FenqileReconciliationService {

    private final FenqileReconciliationDao fenqileReconciliationDao;
    private final AdminServiceImpl adminService;

    @Autowired
    public FenqileReconciliationServiceImpl(FenqileReconciliationDao fenqileReconciliationDao,
                                            AdminServiceImpl adminService) {
        this.fenqileReconciliationDao = fenqileReconciliationDao;
        this.adminService = adminService;
    }

    @Override
    public ICommonDao<FenqileReconciliation, Integer> getDao() {
        return fenqileReconciliationDao;
    }

    public void recon(String orderNos, String remark) {
        Admin admin = adminService.getCurrentUser();

        if (orderNos.contains(Constant.DEFAULT_SPLIT_SEPARATOR)) {
            String[] orderNoList = orderNos.split(Constant.DEFAULT_SPLIT_SEPARATOR);
            for (String orderNo : orderNoList) {
                updateByOrderNo(admin, orderNo, remark);
            }
        } else {
            updateByOrderNo(admin, orderNos, remark);
        }
    }

    private void updateByOrderNo(Admin admin, String orderNo, String remark) {
        FenqileReconciliation reconciliation = fenqileReconciliationDao.findByOrderNo(orderNo);
        reconciliation.setStatus(Constant.IS_RECON);
        reconciliation.setProcessTime(DateUtil.date());
        reconciliation.setAdminId(admin.getId());
        reconciliation.setAdminName(admin.getName());
        reconciliation.setRemark(remark);
        reconciliation.setUpdateTime(DateUtil.date());
        fenqileReconciliationDao.update(reconciliation);
    }
}
