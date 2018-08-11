package com.fulu.game.core.service.impl;

import com.fulu.game.common.enums.MoneyOperateTypeEnum;
import com.fulu.game.common.exception.CashException;
import com.fulu.game.common.exception.ChannelException;
import com.fulu.game.common.exception.OrderException;
import com.fulu.game.core.dao.ChannelCashDetailsDao;
import com.fulu.game.core.dao.ChannelDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.ChannelCashDetailsVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.order.DefaultOrderServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ChannelCashDetailsServiceImpl extends AbsCommonService<ChannelCashDetails, Integer> implements ChannelCashDetailsService {

    @Autowired
    private ChannelCashDetailsDao channelCashDetailsDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private DefaultOrderServiceImpl orderService;

    @Override
    public ICommonDao<ChannelCashDetails, Integer> getDao() {
        return channelCashDetailsDao;
    }

    @Override
    @Transactional
    public ChannelCashDetails addCash(Integer channelId, BigDecimal money, String remark) {
        Admin admin = adminService.getCurrentUser();
        int adminId = admin.getId();
        log.info("操作人id={}", adminId);
        Channel channel = channelService.findById(channelId);
        if (null == channel) {
            throw new ChannelException(ChannelException.ExceptionCode.RECORD_NOT_EXIST);
        }
        BigDecimal oldBalance = channel.getBalance();
        log.info("加款前渠道商余额balance={},加款金额money={}", oldBalance, money);
        BigDecimal newBalance = oldBalance.add(money);
        log.info("加款后渠道商余额newBalance={}", newBalance);
        ChannelCashDetails channelCD = new ChannelCashDetails();
        channelCD.setAdminId(adminId);
        channelCD.setAdminName(admin.getName());
        channelCD.setChannelId(channelId);
        channelCD.setAction(MoneyOperateTypeEnum.CHANNEL_ADD_CASH.getType());
        channelCD.setMoney(money);
        channelCD.setSum(newBalance);
        channelCD.setRemark(remark);
        channelCD.setCreateTime(new Date());
        channelCashDetailsDao.create(channelCD);
        log.info("加款流水记录成功,流水id={}", channelCD.getId());

        channel.setBalance(newBalance);
        channel.setUpdateTime(new Date());
        channelDao.update(channel);
        log.info("更新渠道商余额成功,加款结束");
        return channelCD;
    }

    @Override
    @Transactional
    public ChannelCashDetails cancelCash(Integer channelId, BigDecimal money, String remark) {
        Admin admin = adminService.getCurrentUser();
        int adminId = admin.getId();
        log.info("操作人id={}", adminId);
        Channel channel = channelService.findById(channelId);
        if (null == channel) {
            throw new ChannelException(ChannelException.ExceptionCode.RECORD_NOT_EXIST);
        }
        BigDecimal oldBalance = channel.getBalance();
        log.info("扣款前渠道商余额balance={},扣款金额money={}", oldBalance, money);
        if (money.compareTo(oldBalance) == 1) {
            log.error("管理员扣款异常，余额不足");
            throw new CashException(CashException.ExceptionCode.CASH_EXCEED_EXCEPTION);
        }
        BigDecimal newBalance = oldBalance.add(money.negate());
        log.info("扣款后渠道商余额newBalance={}", newBalance);
        ChannelCashDetails channelCD = new ChannelCashDetails();
        channelCD.setAdminId(adminId);
        channelCD.setAdminName(admin.getName());
        channelCD.setChannelId(channelId);
        channelCD.setAction(MoneyOperateTypeEnum.CHANNEL_ADMIN_CUT.getType());
        channelCD.setMoney(money.negate());
        channelCD.setSum(newBalance);
        channelCD.setRemark(remark);
        channelCD.setCreateTime(new Date());
        channelCashDetailsDao.create(channelCD);
        log.info("加款流水记录成功,流水id={}", channelCD.getId());

        channel.setBalance(newBalance);
        channel.setUpdateTime(new Date());
        channelDao.update(channel);
        log.info("更新渠道商余额成功,加款结束");
        return channelCD;
    }

    @Override
    @Transactional
    public ChannelCashDetails cutCash(Integer channelId, BigDecimal money, String orderNo) {
        log.info("调用渠道商扣款接口，入参channelId={},money={},orderNo={}", channelId, money, orderNo);
        log.info("=====开始校验参数=====");
        Channel channel = channelService.findById(channelId);
        if (null == channel) {
            log.info("渠道商记录不存在");
            throw new ChannelException(ChannelException.ExceptionCode.RECORD_NOT_EXIST);
        }
        if (null == money || money.compareTo(BigDecimal.ZERO) == -1) {
            throw new CashException(CashException.ExceptionCode.CASH_NEGATIVE_EXCEPTION);
        }
        Order order = orderService.findByOrderNo(orderNo);
        if (null == order) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_NOT_EXIST, orderNo);
        }
        BigDecimal oldBalance = channel.getBalance();
        log.info("扣款前渠道商余额balance={},订单金额money={}", oldBalance, money);
        if (money.compareTo(oldBalance) == 1) {
            log.error("渠道商扣款失败，余额不足");
            throw new CashException(CashException.ExceptionCode.CASH_ORDER_FAIL_EXCEPTION);
        }
        log.info("=====校验参数结束=====");

        BigDecimal newBalance = oldBalance.add(money.negate());
        log.info("扣款后渠道商余额newBalance={}", newBalance);
        ChannelCashDetails channelCD = new ChannelCashDetails();
        channelCD.setChannelId(channelId);
        channelCD.setAction(MoneyOperateTypeEnum.CHANNEL_CUT_CASH.getType());
        channelCD.setMoney(money.negate());
        channelCD.setSum(newBalance);
        channelCD.setOrderNo(orderNo);
        channelCD.setRemark("订单扣款");
        channelCD.setCreateTime(new Date());
        channelCashDetailsDao.create(channelCD);
        log.info("扣款流水记录成功,流水id={}", channelCD.getId());

        channel.setBalance(newBalance);
        channel.setUpdateTime(new Date());
        channelDao.update(channel);
        log.info("更新渠道商余额成功,扣款结束");
        return channelCD;
    }

    @Override
    @Transactional
    public ChannelCashDetails refundCash(Integer channelId, BigDecimal money, String orderNo) {
        Admin admin;
        User user;
        int operatorId;
        try {
            admin = adminService.getCurrentUser();
            operatorId = admin.getId();
        } catch (Exception e) {
            user = userService.getCurrentUser();
            operatorId = user.getId();
        }
        log.info("调用渠道商退款接口，操作人id={},入参channelId={},money={},orderNo={}", operatorId, channelId, money, orderNo);
        log.info("=====开始校验参数=====");
        Channel channel = channelService.findById(channelId);
        if (null == channel) {
            log.info("渠道商记录不存在");
            throw new ChannelException(ChannelException.ExceptionCode.RECORD_NOT_EXIST);
        }
        if (money.compareTo(BigDecimal.ZERO) == -1) {
            throw new CashException(CashException.ExceptionCode.CASH_NEGATIVE_EXCEPTION);
        }
        Order order = orderService.findByOrderNo(orderNo);
        if (null == order) {
            throw new OrderException(OrderException.ExceptionCode.ORDER_NOT_EXIST, orderNo);
        }
        log.info("=====校验参数结束=====");

        BigDecimal oldBalance = channel.getBalance();
        log.info("退款前渠道商余额balance={},退款money={}", oldBalance, money);
        BigDecimal newBalance = oldBalance.add(money);
        log.info("退款后渠道商余额newBalance={}", newBalance);
        ChannelCashDetails channelCD = new ChannelCashDetails();
        channelCD.setChannelId(channelId);
        channelCD.setAction(MoneyOperateTypeEnum.CHANNEL_REFUND.getType());
        channelCD.setMoney(money);
        channelCD.setSum(newBalance);
        channelCD.setOrderNo(orderNo);
        channelCD.setRemark("订单退款");
        channelCD.setCreateTime(new Date());
        channelCashDetailsDao.create(channelCD);
        log.info("退款流水记录成功,流水id={}", channelCD.getId());

        channel.setBalance(newBalance);
        channel.setUpdateTime(new Date());
        channelDao.update(channel);
        log.info("更新渠道商余额成功,退款结束");
        return channelCD;
    }

    @Override
    public BigDecimal sumByChannelId(Integer channelId) {
        return channelCashDetailsDao.sumByChannelId(channelId);
    }

    @Override
    public PageInfo<ChannelCashDetails> list(Integer pageNum, Integer pageSize, Integer channelId) {
        PageHelper.startPage(pageNum, pageSize,"create_time desc");
        ChannelCashDetailsVO channelCDVO = new ChannelCashDetailsVO();
        channelCDVO.setChannelId(channelId);
        List<ChannelCashDetails> channelCashDetailsList = channelCashDetailsDao.findByParameter(channelCDVO);
        return new PageInfo<>(channelCashDetailsList);
    }
}
