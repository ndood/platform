package com.fulu.game.core.service.impl;

import com.fulu.game.common.Constant;
import com.fulu.game.core.dao.CdkGroupDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.Cdk;
import com.fulu.game.core.entity.CdkGroup;
import com.fulu.game.core.entity.vo.CdkGroupVO;
import com.fulu.game.core.entity.vo.CdkVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.CdkGroupService;
import com.fulu.game.core.service.CdkService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CdkGroupServiceImpl extends AbsCommonService<CdkGroup, Integer> implements CdkGroupService {

    @Autowired
    private CdkGroupDao cdkGroupDao;
    @Autowired
    private AdminService adminService;
    @Autowired
    private CdkService cdkService;

    @Override
    public ICommonDao<CdkGroup, Integer> getDao() {
        return cdkGroupDao;
    }

    @Override
    @Transactional
    public Boolean generate(CdkGroup cdkGroup) {
        //todo 定制事物传播行为
        Boolean success = true;
        Admin admin = adminService.getCurrentUser();
        int adminId = admin.getId();
        int categoryId = cdkGroup.getCategoryId();
        String type = cdkGroup.getType();
        int amount = cdkGroup.getAmount();
        BigDecimal price = cdkGroup.getPrice();
        log.info("调用cdk生成接口，操作人id={},游戏id={}，类型type={},数量amount={},单价price={}", adminId, categoryId, type, amount, price);
        cdkGroup.setAdminId(adminId);
        cdkGroup.setAdminName(admin.getName());
        cdkGroup.setChannelId(Constant.DEFAULT_CDK_CHANNELID);
        cdkGroup.setChannelName(Constant.DEFAULT_CDK_CHANNEL_NAME);
        cdkGroup.setStatus(true);//默认启用状态
        cdkGroup.setCreateTime(new Date());
        cdkGroup.setUpdateTime(new Date());
        cdkGroupDao.create(cdkGroup);
        log.info("=====生成cdk批次记录完成，批量生成cdk开始=====");
        long start = System.currentTimeMillis();
        int resultNum = cdkService.insertList(cdkGroup);
        long end = System.currentTimeMillis();
        log.info("执行生成数量resultNum={},用时t={}",resultNum,end-start);
        if (resultNum != amount) {
            success = false;
        }
        return success;
    }

    @Override
    public PageInfo<CdkGroup> list(Integer pageNum, Integer pageSize, String orderBy){
        PageHelper.startPage(pageNum,pageSize,orderBy);
        CdkGroupVO cdkGroupVO = new CdkGroupVO();
        List<CdkGroup> list = cdkGroupDao.findByParameter(cdkGroupVO);
        return new PageInfo<>(list);
    }

}
