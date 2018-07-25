package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.AutoReceivingOrderDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.AutoReceivingOrder;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.AutoReceivingOrderService;
import com.fulu.game.core.service.UserTechAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class AutoReceivingOrderServiceImpl extends AbsCommonService<AutoReceivingOrder, Integer> implements AutoReceivingOrderService {

    @Autowired
    private AutoReceivingOrderDao autoReceivingOrderDao;
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserTechAuthService userTechAuthService;


    @Override
    public ICommonDao<AutoReceivingOrder, Integer> getDao() {
        return autoReceivingOrderDao;
    }

    @Override
    public AutoReceivingOrder addAutoReceivingTech(Integer techAuthId, String remark) {
        UserTechAuth userTechAuth = userTechAuthService.findById(techAuthId);
        Admin admin = adminService.getCurrentUser();
        AutoReceivingOrder autoReceivingOrder = new AutoReceivingOrder();
        autoReceivingOrder.setCategoryId(userTechAuth.getCategoryId());
        autoReceivingOrder.setTechAuthId(userTechAuth.getId());
        autoReceivingOrder.setRemark(remark);
        autoReceivingOrder.setUserId(userTechAuth.getUserId());
        autoReceivingOrder.setUserAutoSetting(Boolean.FALSE);
        autoReceivingOrder.setAdminId(admin.getId());
        autoReceivingOrder.setAdminName(admin.getName());
        autoReceivingOrder.setCreateTime(new Date());
        autoReceivingOrder.setUpdateTime(new Date());
        create(autoReceivingOrder);

        return autoReceivingOrder;
    }
}
