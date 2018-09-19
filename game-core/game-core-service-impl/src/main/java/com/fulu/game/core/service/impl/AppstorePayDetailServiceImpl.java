package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.AppstorePayDetailDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.AppstorePayDetail;
import com.fulu.game.core.entity.vo.AppstorePayDetailVO;
import com.fulu.game.core.service.AppstorePayDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AppstorePayDetailServiceImpl extends AbsCommonService<AppstorePayDetail, Integer> implements AppstorePayDetailService {

    @Autowired
    private AppstorePayDetailDao appstorePayDetailDao;

    @Override
    public ICommonDao<AppstorePayDetail, Integer> getDao() {
        return appstorePayDetailDao;
    }


    @Override
    public AppstorePayDetail findByTransactionId(String transactionId) {
        AppstorePayDetailVO param = new AppstorePayDetailVO();
        param.setTransactionId(transactionId);
        List<AppstorePayDetail> list = appstorePayDetailDao.findByParameter(param);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}
