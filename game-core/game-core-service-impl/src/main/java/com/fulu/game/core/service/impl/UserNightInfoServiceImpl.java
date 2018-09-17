package com.fulu.game.core.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.TechAuthStatusEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserNightInfoDao;
import com.fulu.game.core.entity.SalesMode;
import com.fulu.game.core.entity.UserNightInfo;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.UserNightInfoVO;
import com.fulu.game.core.service.SalesModeService;
import com.fulu.game.core.service.UserNightInfoService;
import com.fulu.game.core.service.UserTechAuthService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserNightInfoServiceImpl extends AbsCommonService<UserNightInfo, Integer> implements UserNightInfoService {

    @Autowired
    private UserNightInfoDao userNightInfoDao;
    @Qualifier("adminUserTechAuthServiceImpl")
    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private SalesModeService salesModeService;

    @Override
    public ICommonDao<UserNightInfo, Integer> getDao() {
        return userNightInfoDao;
    }

    @Override
    public PageInfo<UserNightInfo> list(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "info.sort ASC");
        UserNightInfoVO vo = new UserNightInfoVO();
        vo.setDelFlag(Boolean.FALSE);
        List<UserNightInfo> infoList = userNightInfoDao.list(vo);
        return new PageInfo<>(infoList);
    }

    @Override
    public void remove(Integer id) {
        UserNightInfo info = new UserNightInfo();
        info.setId(id);
        info.setUpdateTime(DateUtil.date());
        info.setDelFlag(Boolean.TRUE);
        userNightInfoDao.update(info);
    }

    @Override
    public UserNightInfoVO getNightConfig(Integer userId) {
        //获取此用户所有已通过的认证技能
        List<UserTechAuth> techAuthList = userTechAuthService.findByStatusAndUserId(userId,
                TechAuthStatusEnum.NORMAL.getType());

        List<SalesMode> salesModes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(techAuthList)) {
            for (UserTechAuth meta : techAuthList) {
                Integer cId = meta.getCategoryId();
                salesModes = salesModeService.findByCategory(cId);
            }
        }

        UserNightInfoVO vo = new UserNightInfoVO();
        vo.setUserId(userId);
        vo.setDelFlag(Boolean.FALSE);
        List<UserNightInfo> infoList = userNightInfoDao.findByParameter(vo);
        if (CollectionUtils.isNotEmpty(infoList)) {
            UserNightInfo info = infoList.get(0);
            UserNightInfoVO resultVo = new UserNightInfoVO();
            BeanUtil.copyProperties(info, resultVo);
            resultVo.setAllUserTechs(techAuthList);
            resultVo.setAllSalesModes(salesModes);
            return resultVo;
        } else {
            return null;
        }
    }
}
