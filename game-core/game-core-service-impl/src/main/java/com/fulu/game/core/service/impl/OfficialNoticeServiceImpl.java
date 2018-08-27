package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.OfficialNoticeDao;
import com.fulu.game.core.entity.OfficialNotice;
import com.fulu.game.core.service.OfficialNoticeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OfficialNoticeServiceImpl extends AbsCommonService<OfficialNotice, Integer> implements OfficialNoticeService {

    private final OfficialNoticeDao officialNoticeDao;

    @Autowired
    public OfficialNoticeServiceImpl(OfficialNoticeDao officialNoticeDao) {
        this.officialNoticeDao = officialNoticeDao;
    }


    @Override
    public ICommonDao<OfficialNotice, Integer> getDao() {
        return officialNoticeDao;
    }

    @Override
    public PageInfo<OfficialNotice> list(Integer pageNum, Integer pageSize) {
        String orderBy = "create_time DESC";
        PageHelper.startPage(pageNum, pageSize, orderBy);

        List<OfficialNotice> noticeList = officialNoticeDao.findAll();
        return new PageInfo<>(noticeList);
    }

    @Override
    public void add(OfficialNotice officialNotice) {
        officialNotice.setCreateTime(DateUtil.date());
        officialNotice.setUpdateTime(DateUtil.date());
        officialNoticeDao.create(officialNotice);
    }
}
