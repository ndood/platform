package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.UserInfoAuthRejectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.UserInfoAuthRejectDao;
import com.fulu.game.core.entity.UserInfoAuthReject;
import com.fulu.game.core.service.UserInfoAuthRejectService;

import java.util.List;


@Service
public class UserInfoAuthRejectServiceImpl extends AbsCommonService<UserInfoAuthReject,Integer> implements UserInfoAuthRejectService {

    @Autowired
	private UserInfoAuthRejectDao userInfoAuthRejectDao;

    @Override
    public ICommonDao<UserInfoAuthReject, Integer> getDao() {
        return userInfoAuthRejectDao;
    }

    @Override
    public UserInfoAuthReject findLastRecordByUserId(Integer userId,Integer status) {
        List<UserInfoAuthReject> rejectList = findByUserId(userId,status);
        if(rejectList.isEmpty()){
            return null;
        }
        return rejectList.get(0);
    }



    public List<UserInfoAuthReject> findByUserId(Integer userId,Integer status){
        UserInfoAuthRejectVO param = new UserInfoAuthRejectVO();
        param.setUserId(userId);
        param.setUserInfoAuthStatus(status);
        List<UserInfoAuthReject> rejectList=  userInfoAuthRejectDao.findByParameter(param);
        return rejectList;
    }
}
