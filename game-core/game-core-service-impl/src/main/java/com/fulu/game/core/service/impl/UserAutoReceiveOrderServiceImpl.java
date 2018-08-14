package com.fulu.game.core.service.impl;


import com.fulu.game.common.Constant;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserAutoReceiveOrderDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserAutoReceiveOrder;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.UserAutoReceiveOrderVO;
import com.fulu.game.core.entity.vo.searchVO.UserAutoOrderSearchVO;
import com.fulu.game.core.entity.vo.searchVO.UserInfoAuthSearchVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.UserAutoReceiveOrderService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.UserTechAuthService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.xiaoleilu.hutool.util.CollectionUtil;
import com.xiaoleilu.hutool.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserAutoReceiveOrderServiceImpl extends AbsCommonService<UserAutoReceiveOrder, Integer> implements UserAutoReceiveOrderService {

    @Autowired
    private UserAutoReceiveOrderDao userAutoReceiveOrderDao;
    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;


    @Override
    public ICommonDao<UserAutoReceiveOrder, Integer> getDao() {
        return userAutoReceiveOrderDao;
    }


    @Override
    public UserAutoReceiveOrder addAutoReceivingTech(Integer techAuthId, String remark) {
        UserAutoReceiveOrder userAutoReceiveOrder = userAutoReceiveOrderDao.findByTechIdIncludeDel(techAuthId);
        if (userAutoReceiveOrder != null) {
            userAutoReceiveOrder.setDelFlag(Boolean.FALSE);
            userAutoReceiveOrder.setRemark(remark);
            userAutoReceiveOrder.setUserAutoSetting(Boolean.FALSE);
            userAutoReceiveOrder.setUpdateTime(new Date());
            update(userAutoReceiveOrder);
            return userAutoReceiveOrder;
        }
        UserTechAuth userTechAuth = userTechAuthService.findById(techAuthId);
        Admin admin = adminService.getCurrentUser();
        UserAutoReceiveOrder autoReceivingOrder = new UserAutoReceiveOrder();
        autoReceivingOrder.setCategoryId(userTechAuth.getCategoryId());
        autoReceivingOrder.setTechAuthId(userTechAuth.getId());
        autoReceivingOrder.setRemark(remark);
        autoReceivingOrder.setUserId(userTechAuth.getUserId());
        autoReceivingOrder.setUserAutoSetting(Boolean.FALSE);
        autoReceivingOrder.setOrderNum(0);
        autoReceivingOrder.setOrderCancelNum(0);
        autoReceivingOrder.setOrderCompleteNum(0);
        autoReceivingOrder.setOrderDisputeNum(0);
        autoReceivingOrder.setAdminId(admin.getId());
        autoReceivingOrder.setAdminName(admin.getName());
        autoReceivingOrder.setCreateTime(new Date());
        autoReceivingOrder.setUpdateTime(new Date());
        autoReceivingOrder.setDelFlag(Boolean.FALSE);
        create(autoReceivingOrder);
        return autoReceivingOrder;
    }


    @Override
    public UserAutoReceiveOrder delAutoReceivingTech(Integer techAuthId) {
        UserAutoReceiveOrder userAutoReceiveOrder = findByTechId(techAuthId);
        userAutoReceiveOrder.setDelFlag(Boolean.TRUE);
        update(userAutoReceiveOrder);
        return userAutoReceiveOrder;
    }

    @Override
    public List<UserAutoReceiveOrder> findByUserId(int userId) {
        UserAutoReceiveOrderVO param = new UserAutoReceiveOrderVO();
        param.setUserId(userId);
        return userAutoReceiveOrderDao.findByParameter(param);
    }

    @Override
    public UserAutoReceiveOrder findByUserIdAndCategoryId(int userId, int categoryId) {
        UserAutoReceiveOrderVO param = new UserAutoReceiveOrderVO();
        param.setUserId(userId);
        param.setCategoryId(categoryId);
        List<UserAutoReceiveOrder> autoReceiveOrders = userAutoReceiveOrderDao.findByParameter(param);
        if (autoReceiveOrders.isEmpty()) {
            return null;
        }
        return autoReceiveOrders.get(0);
    }

    @Override
    public UserAutoReceiveOrder findByTechId(int techId) {
        UserAutoReceiveOrderVO param = new UserAutoReceiveOrderVO();
        param.setTechAuthId(techId);
        List<UserAutoReceiveOrder> autoReceiveOrders = userAutoReceiveOrderDao.findByParameter(param);
        if (autoReceiveOrders.isEmpty()) {
            return null;
        }
        return autoReceiveOrders.get(0);
    }


    @Override
    public List<Integer> findUserBySearch(UserAutoOrderSearchVO userAutoOrderSearchVO) {
        return userAutoReceiveOrderDao.findUserBySearch(userAutoOrderSearchVO);
    }

    @Override
    public void activateAutoOrder(int userId, boolean flag) {
        List<UserAutoReceiveOrder> userAutoReceiveOrders = findByUserId(userId);
        for (UserAutoReceiveOrder autoReceiveOrder : userAutoReceiveOrders) {
            autoReceiveOrder.setUserAutoSetting(flag);
            update(autoReceiveOrder);
        }
    }

    @Override
    public void addOrderNum(int userId, int categoryId) {
        UserAutoReceiveOrder autoReceiveOrder = findByUserIdAndCategoryId(userId, categoryId);
        if (autoReceiveOrder == null) {
            return;
        }
        Integer orderNum = autoReceiveOrder.getOrderNum();
        if (orderNum == null) {
            orderNum = 0;
        }
        autoReceiveOrder.setOrderNum(orderNum + 1);
        autoReceiveOrder.setUpdateTime(new Date());
        update(autoReceiveOrder);
    }

    @Override
    public void addOrderCompleteNum(int userId, int categoryId) {
        UserAutoReceiveOrder autoReceiveOrder = findByUserIdAndCategoryId(userId, categoryId);
        if (autoReceiveOrder == null) {
            return;
        }
        Integer orderCompleteNum = autoReceiveOrder.getOrderCompleteNum();
        if (orderCompleteNum == null) {
            orderCompleteNum = 0;
        }
        autoReceiveOrder.setOrderCompleteNum(orderCompleteNum + 1);
        autoReceiveOrder.setUpdateTime(new Date());
        update(autoReceiveOrder);
    }

    @Override
    public void addOrderCancelNum(int userId, int categoryId) {
        UserAutoReceiveOrder autoReceiveOrder = findByUserIdAndCategoryId(userId, categoryId);
        if (autoReceiveOrder == null) {
            return;
        }
        Integer orderCancelNum = autoReceiveOrder.getOrderCancelNum();
        if (orderCancelNum == null) {
            orderCancelNum = 0;
        }
        autoReceiveOrder.setOrderCancelNum(orderCancelNum + 1);
        autoReceiveOrder.setUpdateTime(new Date());
        update(autoReceiveOrder);
    }

    @Override
    public void addOrderDisputeNum(int userId, int categoryId) {
        UserAutoReceiveOrder autoReceiveOrder = findByUserIdAndCategoryId(userId, categoryId);
        if (autoReceiveOrder == null) {
            return;
        }
        Integer orderDisputeNum = autoReceiveOrder.getOrderDisputeNum();
        if (orderDisputeNum == null) {
            orderDisputeNum = 0;
        }
        autoReceiveOrder.setOrderDisputeNum(orderDisputeNum);
        autoReceiveOrder.setUpdateTime(new Date());
        update(autoReceiveOrder);
    }

    @Override
    public List<String> findAllAutoOrderUserHead() {
        List<UserAutoReceiveOrder> allUserAutoReceiveOrder = findAll();
        if (CollectionUtil.isEmpty(allUserAutoReceiveOrder)) {
            return null;
        }
        Set<Integer> userIds = new HashSet<>();
        for (UserAutoReceiveOrder receiveOrder : allUserAutoReceiveOrder) {
            userIds.add(receiveOrder.getUserId());
        }
        Set<String> headSet = new HashSet<>();
        List<Integer> userIdList = new ArrayList<>(userIds);
        List<User> userList = userService.findByUserIds(userIdList, Boolean.TRUE);
        for (User user : userList) {
            headSet.add(user.getHeadPortraitsUrl());
        }

        int length = RandomUtil.randomInt(Constant.MIN_USER_HEAD_COUNT, Constant.MAX_USER_HEAD_COUNT + 1);
        if (headSet.size() <= length) {
            return Lists.newArrayList(headSet);
        } else {
            Set<String> resultHeadSet = RandomUtil.randomEleSet(headSet, length);
            return Lists.newArrayList(resultHeadSet);
        }
    }

    @Override
    public PageInfo<UserAutoReceiveOrderVO> autoReceiveUserInfoAuthList(Integer pageNum,
                                                                        Integer pageSize,
                                                                        UserInfoAuthSearchVO userInfoAuthSearchVO) {
        String orderBy = userInfoAuthSearchVO.getOrderBy();
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "auto.create_time desc";
        }
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<UserAutoReceiveOrderVO> voList =
                userAutoReceiveOrderDao.findAutoReceiveUserInfoAuthList(userInfoAuthSearchVO);
        if (CollectionUtil.isEmpty(voList)) {
            return null;
        }
        return new PageInfo<>(voList);
    }

    @Override
    public List<UserAutoReceiveOrderVO> autoReceiveUserInfoAuthListByVO(UserInfoAuthSearchVO userInfoAuthSearchVO) {
        String orderBy = userInfoAuthSearchVO.getOrderBy();
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "auto.create_time desc";
        }
        userInfoAuthSearchVO.setOrderBy(orderBy);
        List<UserAutoReceiveOrderVO> voList =
                userAutoReceiveOrderDao.findAutoReceiveUserInfoAuthList(userInfoAuthSearchVO);
        if (CollectionUtil.isEmpty(voList)) {
            return null;
        }
        return voList;
    }
}
