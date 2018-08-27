package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.OfficialNoticeDao;
import com.fulu.game.core.entity.OfficialNotice;
import com.fulu.game.core.service.OfficialNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OfficialNoticeServiceImpl extends AbsCommonService<OfficialNotice, Integer> implements OfficialNoticeService {

    @Autowired
    private OfficialNoticeDao officialNoticeDao;


    @Override
    public ICommonDao<OfficialNotice, Integer> getDao() {
        return officialNoticeDao;
    }

}
