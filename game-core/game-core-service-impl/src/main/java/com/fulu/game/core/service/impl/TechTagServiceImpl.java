package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Tag;
import com.fulu.game.core.entity.vo.TechTagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fulu.game.core.dao.TechTagDao;
import com.fulu.game.core.entity.TechTag;
import com.fulu.game.core.service.TechTagService;



@Service
public class TechTagServiceImpl extends AbsCommonService<TechTag,Integer> implements TechTagService {

    @Autowired
	private TechTagDao techTagDao;


    @Override
    public ICommonDao<TechTag, Integer> getDao() {
        return techTagDao;
    }


    @Override
    public int deleteByTechAuthId(Integer techAuthId) {
        return techTagDao.deleteByTechAuthId(techAuthId);
    }

    @Override
    public List<TechTag> findByTechAuthId(Integer techAuthId) {
        if(techAuthId==null){
            return new ArrayList<>();
        }
        TechTagVO techTagVO = new TechTagVO();
        techTagVO.setTechAuthId(techAuthId);
        List<TechTag>  techTagList =   techTagDao.findByParameter(techTagVO);
        return techTagList;
    }

    @Override
    public int updateTechTagByTag(Tag tag) {
        return techTagDao.updateTechTagByTag(tag);
    }
}
