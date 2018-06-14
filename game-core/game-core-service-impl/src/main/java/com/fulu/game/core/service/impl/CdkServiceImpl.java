package com.fulu.game.core.service.impl;

import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.dao.CdkDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Cdk;
import com.fulu.game.core.entity.CdkGroup;
import com.fulu.game.core.entity.vo.CdkVO;
import com.fulu.game.core.service.CdkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CdkServiceImpl extends AbsCommonService<Cdk, Integer> implements CdkService {

    @Autowired
    private CdkDao cdkDao;

    @Override
    public ICommonDao<Cdk, Integer> getDao() {
        return cdkDao;
    }


    @Override
    public Cdk findBySeries(String series) {
        CdkVO param = new CdkVO();
        param.setSeries(series);
        List<Cdk> cdkList = cdkDao.findByParameter(param);
        if(cdkList.isEmpty()){
            return null;
        }
        return cdkList.get(0);
    }

    @Override
    public int insertList(CdkGroup cdkGroup){
        log.info("调用批量生成cdk接口");
        int categoryId = cdkGroup.getCategoryId();
        int amount = cdkGroup.getAmount();
        log.info("预计数量amount={}",amount);
        List<Cdk> cdkList = new ArrayList<>(amount);
        for (int i=0;i<amount;i++){
            Cdk cdk = new Cdk();
            cdk.setSeries(GenIdUtil.getCdkSeries());
            cdk.setGroupId(cdkGroup.getId());
            cdk.setChannelId(cdkGroup.getChannelId());
            cdk.setCategoryId(categoryId);
            cdk.setType(cdkGroup.getType());
            cdk.setPrice(cdkGroup.getPrice());
            cdk.setIsUse(false);
            cdk.setCreateTime(new Date());
            cdk.setUpdateTime(new Date());
            cdkList.add(cdk);
        }
        return cdkDao.insertList(cdkList);
    }
}
