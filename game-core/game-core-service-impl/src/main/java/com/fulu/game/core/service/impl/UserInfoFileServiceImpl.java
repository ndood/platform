package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.entity.vo.UserInfoFileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.fulu.game.core.dao.UserInfoFileDao;
import com.fulu.game.core.entity.UserInfoFile;
import com.fulu.game.core.service.UserInfoFileService;



@Service
public class UserInfoFileServiceImpl extends AbsCommonService<UserInfoFile,Integer> implements UserInfoFileService {

    @Autowired
	private UserInfoFileDao userInfoFileDao;





    @Override
    public ICommonDao<UserInfoFile, Integer> getDao() {
        return userInfoFileDao;
    }

    @Override
    public List<UserInfoFile> findByUserId(Integer userId) {
        UserInfoFileVO userInfoFileVO = new UserInfoFileVO();
        userInfoFileVO.setUserId(userId);
        List<UserInfoFile> userInfoFiles = userInfoFileDao.findByParameter(userInfoFileVO);
        return userInfoFiles;
    }
}
