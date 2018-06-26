package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.PilotOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.PilotOrderDao;
import com.fulu.game.core.entity.PilotOrder;
import com.fulu.game.core.service.PilotOrderService;

import java.util.List;


@Service
public class PilotOrderServiceImpl extends AbsCommonService<PilotOrder,Integer> implements PilotOrderService {

    @Autowired
	private PilotOrderDao pilotOrderDao;



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
        List<PilotOrder> pilotOrderList =    pilotOrderDao.findByParameter(param);
        if(pilotOrderList.isEmpty()){
            return null;
        }
        return pilotOrderList.get(0);
    }


}
