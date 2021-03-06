package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.VirtualProductAttachDao;
import com.fulu.game.core.entity.VirtualProductAttach;
import com.fulu.game.core.entity.vo.VirtualProductAttachVO;
import com.fulu.game.core.service.VirtualProductAttachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class VirtualProductAttachServiceImpl extends AbsCommonService<VirtualProductAttach, Integer> implements VirtualProductAttachService {

    @Autowired
    private VirtualProductAttachDao virtualProductAttachDao;


    @Override
    public ICommonDao<VirtualProductAttach, Integer> getDao() {
        return virtualProductAttachDao;
    }

    @Override
    public List<VirtualProductAttach> findByParameter(VirtualProductAttachVO virtualProductAttachVO) {
        return virtualProductAttachDao.findByParameter(virtualProductAttachVO);
    }

    @Override
    public List<VirtualProductAttachVO> findByOrderProIdUserId(Integer userId, Integer productId) {
        return virtualProductAttachDao.findByOrderProIdUserId(userId,productId);
    }

    @Override
    public int deleteByVirtualProductId(int virtualProductId) {
        return virtualProductAttachDao.deleteByVirtualProductId(virtualProductId);
    }

    @Override
    public List<VirtualProductAttachVO> findDetailByVo(VirtualProductAttachVO virtualProductAttachVO) {
        return virtualProductAttachDao.findDetailByVo(virtualProductAttachVO);
    }


    @Override
    public List<VirtualProductAttach> findByProductId(Integer virtualProductId) {
        VirtualProductAttachVO vpav = new VirtualProductAttachVO();
        vpav.setVirtualProductId(virtualProductId);
        List<VirtualProductAttach> vpaList = virtualProductAttachDao.findByParameter(vpav);
        return vpaList;
    }
}
