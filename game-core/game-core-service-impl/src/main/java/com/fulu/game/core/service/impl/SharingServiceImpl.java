package com.fulu.game.core.service.impl;

import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.SharingDao;
import com.fulu.game.core.entity.Sharing;
import com.fulu.game.core.entity.vo.SharingVO;
import com.fulu.game.core.service.SharingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SharingServiceImpl extends AbsCommonService<Sharing, Integer> implements SharingService {

    @Autowired
    private SharingDao sharingDao;


    @Override
    public ICommonDao<Sharing, Integer> getDao() {
        return sharingDao;
    }

    public PageInfo<Sharing> list(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize, "create_time DESC");
        List<Sharing> list = sharingDao.findByParameter(null);
        return new PageInfo(list);
    }

    public List<Sharing> findByParam(SharingVO sharingVO){
        return sharingDao.findByParameter(sharingVO);
    }

}
