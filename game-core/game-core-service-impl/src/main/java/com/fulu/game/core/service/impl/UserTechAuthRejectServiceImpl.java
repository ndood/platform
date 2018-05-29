package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.UserTechAuthRejectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.UserTechAuthRejectDao;
import com.fulu.game.core.entity.UserTechAuthReject;
import com.fulu.game.core.service.UserTechAuthRejectService;

import java.util.List;


@Service
public class UserTechAuthRejectServiceImpl extends AbsCommonService<UserTechAuthReject,Integer> implements UserTechAuthRejectService {

    @Autowired
	private UserTechAuthRejectDao userTechAuthRejectDao;


    @Override
    public ICommonDao<UserTechAuthReject, Integer> getDao() {
        return userTechAuthRejectDao;
    }

    @Override
    public UserTechAuthReject findLastRecordByTechAuth(Integer techAuthId, Integer status) {
        List<UserTechAuthReject> rejectList = findByTechAuth(techAuthId,status);
        if(rejectList.isEmpty()){
            return null;
        }
        return rejectList.get(0);
    }

    @Override
    public List<UserTechAuthReject> findByTechAuth(Integer techAuthId, Integer status) {
        UserTechAuthRejectVO param = new UserTechAuthRejectVO();
        param.setUserTechAuthId(techAuthId);
        param.setUserTechAuthStatus(status);
        List<UserTechAuthReject> rejectList=  userTechAuthRejectDao.findByParameter(param);
        return rejectList;
    }
}
