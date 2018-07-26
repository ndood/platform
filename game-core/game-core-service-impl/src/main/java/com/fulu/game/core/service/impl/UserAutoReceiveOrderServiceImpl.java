package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.searchVO.UserAutoOrderSearchVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.UserTechAuthService;
import org.bouncycastle.cms.PasswordRecipientId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.UserAutoReceiveOrderDao;
import com.fulu.game.core.entity.UserAutoReceiveOrder;
import com.fulu.game.core.service.UserAutoReceiveOrderService;

import java.util.Date;
import java.util.List;


@Service
public class UserAutoReceiveOrderServiceImpl extends AbsCommonService<UserAutoReceiveOrder,Integer> implements UserAutoReceiveOrderService {

    @Autowired
	private UserAutoReceiveOrderDao userAutoReceiveOrderDao;
    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private AdminService adminService;


    @Override
    public ICommonDao<UserAutoReceiveOrder, Integer> getDao() {
        return userAutoReceiveOrderDao;
    }

    @Override
    public UserAutoReceiveOrder addAutoReceivingTech(Integer techAuthId, String remark) {
        UserTechAuth userTechAuth = userTechAuthService.findById(techAuthId);
        Admin admin = adminService.getCurrentUser();
        UserAutoReceiveOrder autoReceivingOrder = new UserAutoReceiveOrder();
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


    @Override
    public List<Integer> findUserBySearch(UserAutoOrderSearchVO userAutoOrderSearchVO) {
        return userAutoReceiveOrderDao.findUserBySearch(userAutoOrderSearchVO);
    }
}
