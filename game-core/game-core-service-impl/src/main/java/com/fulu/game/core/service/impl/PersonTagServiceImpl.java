package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Tag;
import com.fulu.game.core.entity.vo.PersonTagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.fulu.game.core.dao.PersonTagDao;
import com.fulu.game.core.entity.PersonTag;
import com.fulu.game.core.service.PersonTagService;



@Service
public class PersonTagServiceImpl extends AbsCommonService<PersonTag,Integer> implements PersonTagService {

    @Autowired
	private PersonTagDao personTagDao;


    @Override
    public ICommonDao<PersonTag, Integer> getDao() {
        return personTagDao;
    }

    @Override
    public List<PersonTag> findByUserId(Integer userId) {
        PersonTagVO personTagVO = new PersonTagVO();
        personTagVO.setUserId(userId);
        return personTagDao.findByParameter(personTagVO);
    }

    @Override
    public int deleteByUserId(Integer userId) {
        return personTagDao.deleteByUserId(userId);
    }

    @Override
    public int updatePersonTagByTag(Tag tag){
        return personTagDao.updatePersonTagByTag(tag);
    }
}
