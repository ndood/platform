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

        //计算打款总额sum
        BigDecimal lastSum;
        BigDecimal leftAmount;
        PilotOrderDetails lastOrderDetails = pilotOrderDetailsDao.findLastRecord();
        if(lastOrderDetails == null) {
            lastSum = BigDecimal.ZERO;
            //获取领航账户余额
            leftAmount = pilotOrderService.amountOfProfit(null, null);
            if(leftAmount.compareTo(money) >= 0) {
                details.setLeftAmount(leftAmount.subtract(money));
            }else {
                return false;
            }
        }else {
            lastSum = lastOrderDetails.getSum();
            leftAmount = lastOrderDetails.getLeftAmount();
            if(leftAmount.compareTo(money) >= 0) {
                details.setLeftAmount(leftAmount.subtract(money));
            }else {
                return false;
            }
        }
        details.setSum(lastSum.add(money));
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
}
