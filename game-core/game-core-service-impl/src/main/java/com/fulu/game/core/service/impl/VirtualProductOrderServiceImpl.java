package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.VirtualDetailsTypeEnum;
import com.fulu.game.common.enums.VirtualProductTypeEnum;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualProductOrderDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualProductOrder;
import com.fulu.game.core.entity.vo.VirtualProductOrderVO;
import com.fulu.game.core.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class VirtualProductOrderServiceImpl extends AbsCommonService<VirtualProductOrder, Integer> implements VirtualProductOrderService {

    @Autowired
    private VirtualProductOrderDao virtualProductOrderDao;
    @Autowired
    private UserService userService;
    @Autowired
    private VirtualDetailsService virtualDetailsService;
    @Autowired
    private VirtualProductService virtualProductService;
    //    @Autowired
//    private UserInfoAuthService userInfoAuthService;
    @Autowired
    private OrderService orderService;

    @Override
    public ICommonDao<VirtualProductOrder, Integer> getDao() {
        return virtualProductOrderDao;
    }

    @Override
    public boolean sendGift(Integer fromUserId, Integer targetUserId, Integer virtualProductId) {
        userService.isCurrentUser(fromUserId);
        User fromUser = userService.findById(fromUserId);
        Integer virtualBalance = fromUser.getVirtualBalance();
        if (virtualBalance == null) {
            virtualBalance = 0;
        }
        Integer price = virtualProductService.findPriceById(virtualProductId);
        if (virtualBalance < price) {
            log.error("用户userId：{}的钻石余额不够送礼物，钻石余额：{}，礼物价值：{}", fromUserId, virtualBalance, price);
            throw new ServiceErrorException("用户钻石余额不够送礼物！");
        }

        //todo
        //1、扣钻石，记录流水
        //2、加魅力值
        //3、记录订单表
        User targetUser = userService.findById(targetUserId);
        if (targetUser == null) {
            log.info("当前接收礼物的用户id={}查询数据库不存在", targetUserId);
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        //发起人扣钻石
        userService.calculateVirtualBalance(fromUserId, Math.negateExact(price));
        //记录订单
        VirtualProductOrder order = new VirtualProductOrder();
        order.setOrderNo(generateVirtualProductOrderNo());
        order.setVirtualProductId(virtualProductId);
        order.setPrice(price);
        order.setFromUserId(fromUserId);
        order.setTargetUserId(targetUserId);
        order.setRemark(VirtualProductTypeEnum.VIRTUAL_GIFT.getMsg());
        order.setUpdateTime(DateUtil.date());
        order.setCreateTime(DateUtil.date());
        create(order);

        //记录流水
        virtualDetailsService.createVirtualDetails(fromUserId, virtualProductId, Math.negateExact(price),
                VirtualDetailsTypeEnum.VIRTUAL_MONEY, VirtualProductTypeEnum.VIRTUAL_GIFT);
        //接收人加魅力值
//        userInfoAuthService.modifyCharm(targetUserId, price);
        //记录流水
//        virtualDetailsService.createVirtualDetails(targetUserId, price, VirtualProductTypeEnum.VIRTUAL_GIFT);

        return true;
    }

    /**
     * 生成虚拟商品订单号
     *
     * @return 订单号字符串
     */
    @Override
    public String generateVirtualProductOrderNo() {
        String orderNo = "V" + GenIdUtil.GetOrderNo();
        if (orderService.findByOrderNo(orderNo) == null) {
            return orderNo;
        } else {
            return generateVirtualProductOrderNo();
        }
    }

    @Override
    public List<VirtualProductOrder> findByParameter(VirtualProductOrderVO virtualProductOrderVO) {
        return virtualProductOrderDao.findByParameter(virtualProductOrderVO);
    }
}
