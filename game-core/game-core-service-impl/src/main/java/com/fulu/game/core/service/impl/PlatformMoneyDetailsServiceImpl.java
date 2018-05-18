package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.PlatFormMoneyTypeEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.PlatformMoneyDetailsDao;
import com.fulu.game.core.entity.PlatformMoneyDetails;
import com.fulu.game.core.service.PlatformMoneyDetailsService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Service
@Transactional
public class PlatformMoneyDetailsServiceImpl implements PlatformMoneyDetailsService{

    @Autowired
	private PlatformMoneyDetailsDao platformMoneyDetailsDao;

    /**
     * 记录订单相关的平台流水
     * @param platFormMoneyTypeEnum
     * @param orderNo
     * @param money
     * @return
     */
    @Override
    public  synchronized PlatformMoneyDetails  createOrderDetails(PlatFormMoneyTypeEnum platFormMoneyTypeEnum, String orderNo, BigDecimal money) {
        String remark = platFormMoneyTypeEnum.getType()+"["+orderNo+"]";
        return createPlatformMoneyDetails(remark,money);
    }

    /**
     * 记录加零钱相关的平台流水
     * @param remark
     * @param money
     * @return
     */
    @Override
    public synchronized PlatformMoneyDetails  createSmallChangeDetails(String remark,Integer userId,BigDecimal money){
        String remarkResult = PlatFormMoneyTypeEnum.SMALLCHANGE.getType()+"["+userId+"]"+remark;
        return createPlatformMoneyDetails(remarkResult,money);
    }

    @Transactional
    protected synchronized PlatformMoneyDetails createPlatformMoneyDetails(String remark,BigDecimal money){
        PlatformMoneyDetails lastMoneyDetails = platformMoneyDetailsDao.findLastMoneyDetails();
        BigDecimal sum = new BigDecimal(0);
        if(lastMoneyDetails!=null){
            sum = lastMoneyDetails.getSum();
        }
        BigDecimal newSum = sum.add(money);
        PlatformMoneyDetails platformMoneyDetails = new PlatformMoneyDetails();
        platformMoneyDetails.setMoney(money);
        platformMoneyDetails.setSum(newSum);
        platformMoneyDetails.setRemark(remark);
        platformMoneyDetails.setCreateTime(new Date());
        create(platformMoneyDetails);
        return platformMoneyDetails;
    }

    @Transactional
    protected int create(PlatformMoneyDetails platformMoneyDetails){
        return platformMoneyDetailsDao.create(platformMoneyDetails);
    }


}
