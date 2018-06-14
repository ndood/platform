package com.fulu.game.core.service.impl;

import com.fulu.game.common.enums.MoneyOperateTypeEnum;
import com.fulu.game.common.exception.CommonException;
import com.fulu.game.core.dao.ChannelCashDetailsDao;
import com.fulu.game.core.dao.ChannelDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.Channel;
import com.fulu.game.core.entity.ChannelCashDetails;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.ChannelCashDetailsService;
import com.fulu.game.core.service.ChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

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
    private ChannelService channelService;

    @Override
    public ICommonDao<ChannelCashDetails, Integer> getDao() {
        return channelCashDetailsDao;
    }

    @Override
    @Transactional
    public ChannelCashDetails addCash(Integer channelId, BigDecimal money, String remark){
        Admin admin = adminService.getCurrentUser();
        int adminId = admin.getId();
        log.info("操作人id={}",adminId);
        Channel channel = channelService.findById(channelId);
        if (null == channel){
            throw new CommonException(CommonException.ExceptionCode.RECORD_NOT_EXSISTS);
        }
        BigDecimal oldBalance = channel.getBalance();
        log.info("加款前渠道商余额balance={},加款金额money={}",oldBalance,money);
        BigDecimal newBalance = oldBalance.add(money);
        log.info("加款后渠道商余额newBalance={}",newBalance);
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
        log.info("加款流水记录成功,流水id={}",channelCD.getId());

        channel.setBalance(newBalance);
        channel.setUpdateTime(new Date());
        channelDao.update(channel);
        log.info("更新渠道商余额成功,加款结束");
        return channelCD;
    }

    @Override
    public ChannelCashDetails cutCash(Integer channelId, BigDecimal money, String orderNo){
        //todo 渠道商扣款
        return null;
    }

}
