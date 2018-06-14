package com.fulu.game.core.service.impl;

import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.core.dao.CdkDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Cdk;
import com.fulu.game.core.entity.CdkGroup;
import com.fulu.game.core.entity.vo.CdkVO;
import com.fulu.game.core.service.CdkService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (cdkList.isEmpty()) {
            return null;
        }
        return cdkList.get(0);
    }

    @Override
    public int insertList(CdkGroup cdkGroup) {
        log.info("调用批量生成cdk接口");
        int categoryId = cdkGroup.getCategoryId();
        int amount = cdkGroup.getAmount();
        log.info("预计数量amount={}", amount);
        List<Cdk> cdkList = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
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

    @Override
    public PageInfo<CdkVO> list(Integer pageNum, Integer pageSize, String series, String orderBy) {
        PageHelper.startPage(pageNum, pageSize, orderBy);
        CdkVO cdkVO = new CdkVO();
        cdkVO.setSeries(series);
        cdkVO.setIsUse(true);
        List<CdkVO> cdkList = cdkDao.listByParam(cdkVO);
        return new PageInfo<>(cdkList);
    }

    @Override
    public int count(Integer groupId, Boolean b) {
        CdkVO cdkVO = new CdkVO();
        cdkVO.setIsUse(b);
        cdkVO.setGroupId(groupId);
        return cdkDao.count(cdkVO);
    }
}
