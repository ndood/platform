package com.fulu.game.core.service.impl;


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
public class PlatformMoneyDetailsServiceImpl extends AbsCommonService<PlatformMoneyDetails,Integer> implements PlatformMoneyDetailsService {

    @Autowired
	private PlatformMoneyDetailsDao platformMoneyDetailsDao;

    @Override
    public ICommonDao<PlatformMoneyDetails, Integer> getDao() {
        return platformMoneyDetailsDao;
    }


    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public PlatformMoneyDetails createOrderDetails(String orderNo,  BigDecimal money) {
        PageHelper.startPage(1,1,"create_time desc");
        List<PlatformMoneyDetails> list = platformMoneyDetailsDao.findAll();
        BigDecimal sum = new BigDecimal(0);
        if(!list.isEmpty()){
            sum = list.get(0).getSum();
        }
        BigDecimal newSum = sum.add(money);
        PlatformMoneyDetails platformMoneyDetails = new PlatformMoneyDetails();
        platformMoneyDetails.setMoney(money);
        platformMoneyDetails.setSum(newSum);
        platformMoneyDetails.setRemark(orderNo+":分润");
        platformMoneyDetails.setCreateTime(new Date());
        create(platformMoneyDetails);
        return platformMoneyDetails;
    }
}
