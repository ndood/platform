package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.PilotOrderDetailsDao;
import com.fulu.game.core.entity.PilotOrderDetails;
import com.fulu.game.core.service.PilotOrderDetailsService;

import java.math.BigDecimal;
import java.util.Date;


@Service
public class PilotOrderDetailsServiceImpl extends AbsCommonService<PilotOrderDetails,Integer> implements PilotOrderDetailsService {

    @Autowired
	private PilotOrderDetailsDao pilotOrderDetailsDao;
    @Autowired
    private AdminService adminService;

    @Override
    public ICommonDao<PilotOrderDetails, Integer> getDao() {
        return pilotOrderDetailsDao;
    }

    @Override
    public boolean remit(BigDecimal money, String remark) {
        //计算总额sum
        BigDecimal lastSum  = pilotOrderDetailsDao.findLastRecordSum();
        if(lastSum == null) {
            lastSum = BigDecimal.ZERO;
        }

        Admin admin = adminService.getCurrentUser();
        PilotOrderDetails details = new PilotOrderDetails();
        details.setRemark(remark);
        details.setMoney(money);
        details.setSum(lastSum.add(money));
        details.setAdminId(admin.getId());
        details.setAdminName(admin.getName());
        details.setCreateTime(new Date());
        int result = pilotOrderDetailsDao.create(details);
        if(result <= 0) {
            return false;
        }
        return true;
    }
}
