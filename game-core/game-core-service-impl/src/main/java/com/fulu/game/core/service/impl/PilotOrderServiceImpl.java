package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.vo.PilotOrderVO;
import com.fulu.game.core.entity.vo.searchVO.OrderSearchVO;
import com.fulu.game.core.service.AdminService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.PilotOrderDao;
import com.fulu.game.core.entity.PilotOrder;
import com.fulu.game.core.service.PilotOrderService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Service
public class PilotOrderServiceImpl extends AbsCommonService<PilotOrder,Integer> implements PilotOrderService {

    @Autowired
	private PilotOrderDao pilotOrderDao;
    @Autowired
    private AdminService adminService;

    @Override
    public PageInfo<PilotOrderVO> findVoList(int pageNum,
                                             int pageSize,
                                             String orderBy,
                                             OrderSearchVO orderSearchVO){
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "create_time DESC";
        }
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<PilotOrderVO> list = pilotOrderDao.findVoList(orderSearchVO);
        return new PageInfo(list);
    }


    @Override
    public BigDecimal amountOfProfit(Date startTime, Date endTime) {
        BigDecimal amount = pilotOrderDao.amountOfProfit(startTime,endTime);
        return amount;
    }


    @Override
    public ICommonDao<PilotOrder, Integer> getDao() {
        return pilotOrderDao;
    }

    @Override
    public PilotOrder findByOrderNo(String orderNo) {
        if(orderNo==null){
            return null;
        }
        PilotOrderVO param = new PilotOrderVO();
        param.setOrderNo(orderNo);
        List<PilotOrder> pilotOrderList = pilotOrderDao.findByParameter(param);
        if(pilotOrderList.isEmpty()){
            return null;
        }
        return pilotOrderList.get(0);
    }

    @Override
    public boolean alterAdminRemark(Integer orderId, String adminRemark) {
        Admin admin = adminService.getCurrentUser();

        PilotOrder pilotOrder = new PilotOrder();
        pilotOrder.setId(orderId);
        pilotOrder.setUpdateTime(new Date());
        pilotOrder.setAdminRemark(adminRemark);
        pilotOrder.setAdminId(admin.getId());
        pilotOrder.setAdminName(admin.getName());
        Integer result = pilotOrderDao.update(pilotOrder);
        if(result <= 0) {
            return false;
        }
        return true;
    }
}
