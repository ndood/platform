package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualPayOrderDao;
import com.fulu.game.core.entity.VirtualPayOrder;
import com.fulu.game.core.entity.vo.VirtualPayOrderVO;
import com.fulu.game.core.service.VirtualPayOrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class VirtualPayOrderServiceImpl extends AbsCommonService<VirtualPayOrder, Integer> implements VirtualPayOrderService {

    @Autowired
    private VirtualPayOrderDao virtualPayOrderDao;

    @Override
    public ICommonDao<VirtualPayOrder, Integer> getDao() {
        return virtualPayOrderDao;
    }

    @Override
    public VirtualPayOrder findByOrderNo(String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            return null;
        }

        return virtualPayOrderDao.findByOrderNo(orderNo);
    }

    @Override
    public PageInfo<VirtualPayOrderVO> chargeList(VirtualPayOrderVO payOrderVO, Integer pageNum, Integer pageSize, String orderBy) {
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "vpo.pay_time DESC";
        }

        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<VirtualPayOrderVO> voList = virtualPayOrderDao.chargeList(payOrderVO);
        return new PageInfo<>(voList);
    }

    @Override
    public PageInfo<VirtualPayOrderVO> chargeList(VirtualPayOrderVO payOrderVO) {
        PageHelper.startPage("vpo.pay_time DESC");
        List<VirtualPayOrderVO> voList = virtualPayOrderDao.chargeList(payOrderVO);
        return new PageInfo<>(voList);
    }
}
