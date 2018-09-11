package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualDetailsDao;
import com.fulu.game.core.entity.VirtualDetails;
import com.fulu.game.core.entity.vo.VirtualDetailsVO;
import com.fulu.game.core.service.VirtualDetailsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class VirtualDetailsServiceImpl extends AbsCommonService<VirtualDetails, Integer> implements VirtualDetailsService {

    @Autowired
    private VirtualDetailsDao virtualDetailsDao;

    @Override
    public ICommonDao<VirtualDetails, Integer> getDao() {
        return virtualDetailsDao;
    }

    @Override
    public PageInfo<VirtualDetails> findByParameterWithPage(VirtualDetailsVO virtualDetailsVO, Integer pageSize, Integer pageNum , String orderBy) {
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<VirtualDetails> list = virtualDetailsDao.findByParameter(virtualDetailsVO);
        return new PageInfo(list);
    }
}
