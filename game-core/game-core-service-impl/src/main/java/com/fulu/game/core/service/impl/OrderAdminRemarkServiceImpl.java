package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.OrderAdminRemarkVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.OrderAdminRemarkDao;
import com.fulu.game.core.entity.OrderAdminRemark;
import com.fulu.game.core.service.OrderAdminRemarkService;

import java.util.Date;
import java.util.List;


@Service
public class OrderAdminRemarkServiceImpl extends AbsCommonService<OrderAdminRemark,Integer> implements OrderAdminRemarkService {

    @Autowired
	private OrderAdminRemarkDao orderAdminRemarkDao;



    @Override
    public ICommonDao<OrderAdminRemark, Integer> getDao() {
        return orderAdminRemarkDao;
    }

    @Override
    public void saveAdminOrderRemark(OrderAdminRemark oar) {
        
        //判断备注是否存在

        OrderAdminRemarkVO param = new OrderAdminRemarkVO();
        param.setOrderId(oar.getOrderId());
        param.setAgentAdminId(oar.getAgentAdminId());

        List<OrderAdminRemark> resultOar = orderAdminRemarkDao.findByParameter(param);
        
        //更新备注
        if(CollectionUtils.isNotEmpty(resultOar)){
            oar.setId(resultOar.get(0).getId());
            oar.setUpdateTime(new Date());
            orderAdminRemarkDao.update(oar);
        }else{
            //新增备注
            oar.setCreateTime(new Date());
            orderAdminRemarkDao.create(oar);
        }
        
    }
}
