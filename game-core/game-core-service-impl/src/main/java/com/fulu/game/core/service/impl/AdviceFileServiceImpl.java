package com.fulu.game.core.service.impl;

import com.fulu.game.core.dao.AdviceFileDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.AdviceFile;
import com.fulu.game.core.service.AdviceFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AdviceFileServiceImpl extends AbsCommonService<AdviceFile, Integer> implements AdviceFileService {

    @Autowired
    private AdviceFileDao adviceFileDao;

    @Override
    public ICommonDao<AdviceFile, Integer> getDao() {
        return adviceFileDao;
    }

    public void save(String[] advicePicUrls, Integer adviceId) {
        if (advicePicUrls == null || advicePicUrls.length == 0 || adviceId == null) {
            return;
        }
        List<AdviceFile> list = new ArrayList<>();
        for (int i = 0; i < advicePicUrls.length; i++) {
            AdviceFile adviceFile = new AdviceFile();
            adviceFile.setAdviceId(adviceId);
            adviceFile.setUrl(advicePicUrls[i]);
            adviceFile.setCreateTime(new Date());
            list.add(adviceFile);
        }
        adviceFileDao.insertList(list);
    }

}
