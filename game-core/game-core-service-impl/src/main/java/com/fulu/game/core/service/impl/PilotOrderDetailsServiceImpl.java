package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.PilotOrderDetailsDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.PilotOrderDetails;
import com.fulu.game.core.entity.vo.PilotOrderDetailsVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.PilotOrderDetailsService;
import com.fulu.game.core.service.PilotOrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Service
public class PilotOrderDetailsServiceImpl extends AbsCommonService<PilotOrderDetails,Integer> implements PilotOrderDetailsService {

    @Autowired
	private PilotOrderDetailsDao pilotOrderDetailsDao;
    @Autowired
    private PilotOrderService pilotOrderService;
    @Autowired
    private AdminService adminService;

    @Override
    public ICommonDao<PilotOrderDetails, Integer> getDao() {
        return pilotOrderDetailsDao;
    }

    @Override
    public boolean remit(BigDecimal money, String remark) {
        Admin admin = adminService.getCurrentUser();
        PilotOrderDetails details = new PilotOrderDetails();
        details.setRemark(remark);
        details.setMoney(money);
        details.setAdminId(admin.getId());
        details.setAdminName(admin.getName());
        details.setCreateTime(new Date());

        //已打款总额sum
        BigDecimal remitSum;
        //获取领航订单获利总金额
        BigDecimal totalProfit = pilotOrderService.amountOfProfit(null, null);
        PilotOrderDetails lastOrderDetails = pilotOrderDetailsDao.findLastRecord();
        if(lastOrderDetails == null) {
            remitSum = BigDecimal.ZERO;
        }else {
            remitSum = lastOrderDetails.getSum();
        }

        totalProfit = totalProfit.subtract(remitSum);
        if(totalProfit.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if(totalProfit.compareTo(money) >= 0) {
            details.setLeftAmount(totalProfit.subtract(money));
        }else {
            return false;
        }

        details.setSum(remitSum.add(money));
        int result = pilotOrderDetailsDao.create(details);
        if(result <= 0) {
            return false;
        }
        return true;
    }

    @Override
    public PageInfo<PilotOrderDetailsVO> findDetailsList(Integer pageNum, Integer pageSize) {
        String orderBy = "create_time desc";
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<PilotOrderDetails> list = pilotOrderDetailsDao.findAll();
        return new PageInfo(list);
    }

    @Override
    public BigDecimal leftAmount() {
        //获取领航订单获利总金额
        BigDecimal totalProfit = pilotOrderService.amountOfProfit(null, null);
        PilotOrderDetails lastOrderDetails = pilotOrderDetailsDao.findLastRecord();
        if(lastOrderDetails == null) {
            return totalProfit;
        }else {
            int result = totalProfit.subtract(lastOrderDetails.getSum()).compareTo(BigDecimal.ZERO);
            if(result <= 0) {
                return BigDecimal.ZERO;
            }
            return totalProfit.subtract(lastOrderDetails.getSum());
        }
    }
}
