package com.fulu.game.core.service.impl;


import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualProductOrderDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualProductOrder;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.VirtualProductOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class VirtualProductOrderServiceImpl extends AbsCommonService<VirtualProductOrder, Integer> implements VirtualProductOrderService {

    @Autowired
    private VirtualProductOrderDao virtualProductOrderDao;
    @Autowired
    private UserService userService;

    @Override
    public ICommonDao<VirtualProductOrder, Integer> getDao() {
        return virtualProductOrderDao;
    }

    @Override
    public boolean sendGift(Integer fromUserId, Integer targetUserId, Integer virtualProductId) {
        //todo
        //1、扣钻石
        //2、加魅力值
        //3、记录订单表
        userService.isCurrentUser(fromUserId);
        User targetUser = userService.findById(targetUserId);
        if(targetUser == null) {
            log.info("当前用户id={}查询数据库不存在", targetUser.getId());
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }

        //扣钻石
        userService.calculateVirtualBalance(fromUserId, virtualProductId);



        return false;
    }
}
