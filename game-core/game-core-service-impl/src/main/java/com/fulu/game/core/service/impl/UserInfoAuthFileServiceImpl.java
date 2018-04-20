package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.UserInfoAuthFileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.fulu.game.core.dao.UserInfoAuthFileDao;
import com.fulu.game.core.entity.UserInfoAuthFile;
import com.fulu.game.core.service.UserInfoAuthFileService;



@Service
public class UserInfoAuthFileServiceImpl extends AbsCommonService<UserInfoAuthFile,Integer> implements UserInfoAuthFileService {

    @Autowired
	private UserInfoAuthFileDao userInfoAuthFileDao;



    @Override
    public ICommonDao<UserInfoAuthFile, Integer> getDao() {
        return userInfoAuthFileDao;
    }

    @Override
    public List<UserInfoAuthFile> findByUserAuthIdAndType(Integer userAuthId, Integer fileType) {
        UserInfoAuthFileVO userInfoAuthFileVO = new UserInfoAuthFileVO();
        userInfoAuthFileVO.setInfoAuthId(userAuthId);
        userInfoAuthFileVO.setType(fileType);
        List<UserInfoAuthFile> userInfoAuthFiles =userInfoAuthFileDao.findByParameter(userInfoAuthFileVO);
        return userInfoAuthFiles;
    }
}
