package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.VirtualDetailsRemarkEnum;
import com.fulu.game.common.enums.VirtualDetailsTypeEnum;
import com.fulu.game.common.enums.VirtualProductTypeEnum;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.exception.VirtualProductException;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualProductOrderDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualDetails;
import com.fulu.game.core.entity.VirtualProduct;
import com.fulu.game.core.entity.VirtualProductOrder;
import com.fulu.game.core.entity.vo.VirtualProductOrderVO;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.VirtualDetailsService;
import com.fulu.game.core.service.VirtualProductOrderService;
import com.fulu.game.core.service.VirtualProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    @Override
    public ICommonDao<VirtualProductOrder, Integer> getDao() {
        return virtualProductOrderDao;
    }

    /**
     * 赠送礼物
     *
     * @param targetUserId     接收人id
     * @param virtualProductId 虚拟商品id
     * @return 虚拟订单Bean
     */
    @Override
    public VirtualProductOrder sendGift(Integer targetUserId, Integer virtualProductId) {
        return createVirtualOrder(userService.getCurrentUser().getId(), targetUserId, virtualProductId,1);
    }

    /**
     * 创建虚拟订单
     *
     * @param fromUserId       发起人id
     * @param targetUserId     接收人id
     * @param virtualProductId 虚拟商品id
     * @param amount 虚拟商品数量
     * @return 虚拟商品订单
     */
    @Override
    public VirtualProductOrder createVirtualOrder(int fromUserId, int targetUserId, int virtualProductId,int amount) {
        User fromUser = userService.findById(fromUserId);
        if (fromUser == null) {
            log.info("发起人用户id：{}查询数据库不存在", fromUserId);
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }

        Long virtualBalance = fromUser.getVirtualBalance() == null ? 0 : fromUser.getVirtualBalance();
        VirtualProduct virtualProduct = virtualProductService.findById(virtualProductId);
        if (virtualProduct == null) {
            log.error("虚拟商品id：{}不存在", virtualProductId);
            throw new VirtualProductException(VirtualProductException.ExceptionCode.NOT_EXIST);
        }

        Long price = new BigDecimal(virtualProduct.getPrice()).multiply(new BigDecimal(amount)).longValue();
        if (virtualBalance < price) {
            log.error("用户userId：{}的钻石余额不够支付虚拟商品，钻石余额：{}，虚拟商品价格：{}",
                    fromUser.getId(), virtualBalance, price);
            throw new VirtualProductException(VirtualProductException.ExceptionCode.VIRTUAL_BALANCE_NOT_ENOUGH_EXCEPTION);
        }

        User targetUser = userService.findById(targetUserId);
        if (targetUser == null) {
            log.info("当前接收用户id：{}查询数据库不存在", targetUserId);
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }

        Integer type = virtualProduct.getType();

        //记录订单
        VirtualProductOrder order = new VirtualProductOrder();
        order.setOrderNo(generateVirtualProductOrderNo());
        order.setVirtualProductId(virtualProductId);
        order.setPrice(price);
        order.setUnitPrice(virtualProduct.getPrice());
        order.setAmount(amount);
        order.setFromUserId(fromUser.getId());
        order.setTargetUserId(targetUserId);
        order.setRemark(VirtualProductTypeEnum.getMsgByType(type));
        order.setUpdateTime(DateUtil.date());
        order.setCreateTime(DateUtil.date());
        create(order);

        //发起人扣钻石
        fromUser = userService.modifyVirtualBalance(fromUser, Math.negateExact(order.getPrice()));

        //记录发起人流水
        VirtualDetails details = new VirtualDetails();
        details.setUserId(fromUser.getId());
        details.setRelevantNo(order.getOrderNo());
        details.setSum(fromUser.getVirtualBalance());
        details.setMoney(Math.negateExact(order.getPrice()));
        details.setType(VirtualDetailsTypeEnum.VIRTUAL_MONEY.getType());
        details.setRemark(VirtualDetailsRemarkEnum.getMsgByType(type));
        details.setCreateTime(DateUtil.date());
        virtualDetailsService.create(details);

        //接收人加魅力值
        targetUser = userService.modifyCharm(targetUser, order.getPrice());
        //记录接收人流水
        VirtualDetails targetDetails = new VirtualDetails();
        targetDetails.setUserId(targetUser.getId());
        targetDetails.setRelevantNo(order.getOrderNo());
        targetDetails.setSum(targetUser.getCharm());
        targetDetails.setMoney(Math.negateExact(order.getPrice()));
        targetDetails.setType(VirtualDetailsTypeEnum.CHARM.getType());
        //设置接收人的remark备注信息
        String targetUserRemark;
        if (VirtualDetailsRemarkEnum.GIFT_COST.getType().equals(type)) {
            targetUserRemark = VirtualDetailsRemarkEnum.GIFT_RECEIVE.getMsg();
        } else {
            targetUserRemark = VirtualDetailsRemarkEnum.getMsgByType(type);
        }
        targetDetails.setRemark(targetUserRemark);
        targetDetails.setCreateTime(DateUtil.date());
        virtualDetailsService.create(targetDetails);

        return order;
    }

    /**
     * 生成虚拟商品订单号
     *
     * @return 订单号字符串
     */
    @Override
    public String generateVirtualProductOrderNo() {
        String orderNo = "V_" + GenIdUtil.GetOrderNo();
        if (findByOrderNo(orderNo) == null) {
            return orderNo;
        } else {
            return generateVirtualProductOrderNo();
        }
    }

    @Override
    public VirtualProductOrder findByOrderNo(String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            return null;
        }

        return virtualProductOrderDao.findByOrderNo(orderNo);
    }

    @Override
    public List<VirtualProductOrder> findByParameter(VirtualProductOrderVO virtualProductOrderVO) {
        return virtualProductOrderDao.findByParameter(virtualProductOrderVO);
    }


    @Override
    public boolean isAlreadyUnlock(Integer userId, Integer virtualProductId) {
        VirtualProductOrderVO vpo = new VirtualProductOrderVO();
        vpo.setFromUserId(userId);
        vpo.setVirtualProductId(virtualProductId);
        List<VirtualProductOrder> vpList = virtualProductOrderDao.findByParameter(vpo);
        if (CollectionUtils.isEmpty(vpList)) {
            return false;
        } else {
            return true;
        }
    }
}
