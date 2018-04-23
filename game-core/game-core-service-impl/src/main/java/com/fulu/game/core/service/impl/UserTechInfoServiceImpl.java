package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.UserTechInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.fulu.game.core.dao.UserTechInfoDao;
import com.fulu.game.core.entity.UserTechInfo;
import com.fulu.game.core.service.UserTechInfoService;



@Service
public class UserTechInfoServiceImpl extends AbsCommonService<UserTechInfo,Integer> implements UserTechInfoService {

    @Autowired
	private UserTechInfoDao userTechInfoDao;


    @Override
    public ICommonDao<UserTechInfo, Integer> getDao() {
        return userTechInfoDao;
    }

    @Override
    public int deleteByTechAuthId(Integer techAuthId) {
        return userTechInfoDao.deleteByTechAuthId(techAuthId);
    }

    public List<UserTechInfo> findByTechAuthId(Integer techAuthId){
        UserTechInfoVO userTechInfoVO = new UserTechInfoVO();
        userTechInfoVO.setTechAuthId(techAuthId);
        return userTechInfoDao.findByParameter(userTechInfoVO);
    }
}
