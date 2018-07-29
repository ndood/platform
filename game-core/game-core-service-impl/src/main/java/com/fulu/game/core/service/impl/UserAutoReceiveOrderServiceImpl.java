package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.UserAutoReceiveOrderVO;
import com.fulu.game.core.entity.vo.searchVO.UserAutoOrderSearchVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.OrderShareProfitService;
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


    public List<UserAutoReceiveOrder> findByUserId(int userId){
        UserAutoReceiveOrderVO param = new UserAutoReceiveOrderVO();
        param.setUserId(userId);
        return userAutoReceiveOrderDao.findByParameter(param);
    }


    public UserAutoReceiveOrder findByUserIdAndCategoryId(int userId,int categoryId){
        UserAutoReceiveOrderVO param = new UserAutoReceiveOrderVO();
        param.setUserId(userId);
        param.setCategoryId(categoryId);
        List<UserAutoReceiveOrder> autoReceiveOrders = userAutoReceiveOrderDao.findByParameter(param);
        if(autoReceiveOrders.isEmpty()){
            return null;
        }
        return autoReceiveOrders.get(0);
    }


    @Override
    public List<Integer> findUserBySearch(UserAutoOrderSearchVO userAutoOrderSearchVO) {
        return userAutoReceiveOrderDao.findUserBySearch(userAutoOrderSearchVO);
    }


    public void activateAutoOrder(int userId,boolean flag){
        List<UserAutoReceiveOrder> userAutoReceiveOrders = findByUserId(userId);
        for(UserAutoReceiveOrder autoReceiveOrder : userAutoReceiveOrders){
            autoReceiveOrder.setUserAutoSetting(flag);
        }
    }

    @Override
    public void addOrderCompleteNum(int userId, int categoryId) {
        UserAutoReceiveOrder autoReceiveOrder = findByUserIdAndCategoryId(userId,categoryId);
        Integer orderCompleteNum =  autoReceiveOrder.getOrderCompleteNum();
        if(orderCompleteNum==null){
            orderCompleteNum = 0;
        }
        autoReceiveOrder.setOrderCompleteNum(orderCompleteNum+1);
        autoReceiveOrder.setUpdateTime(new Date());
        update(autoReceiveOrder);
    }

    @Override
    public void addOrderCancelNum(int userId, int categoryId) {
        UserAutoReceiveOrder autoReceiveOrder = findByUserIdAndCategoryId(userId,categoryId);
        Integer orderCancelNum =  autoReceiveOrder.getOrderCancelNum();
        if(orderCancelNum==null){
            orderCancelNum = 0;
        }
        autoReceiveOrder.setOrderCancelNum(orderCancelNum+1);
        autoReceiveOrder.setUpdateTime(new Date());
        update(autoReceiveOrder);
    }

    @Override
    public void addOrderDisputeNum(int userId, int categoryId) {
        UserAutoReceiveOrder autoReceiveOrder = findByUserIdAndCategoryId(userId,categoryId);
        Integer orderDisputeNum = autoReceiveOrder.getOrderDisputeNum();
        if(orderDisputeNum==null){
            orderDisputeNum = 0 ;
        }
        autoReceiveOrder.setOrderDisputeNum(orderDisputeNum);
        autoReceiveOrder.setUpdateTime(new Date());
        update(autoReceiveOrder);
    }

}
