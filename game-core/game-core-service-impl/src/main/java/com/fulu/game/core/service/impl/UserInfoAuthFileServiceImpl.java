package com.fulu.game.core.service.impl;


import com.fulu.game.common.utils.OssUtil;
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
    @Autowired
    private OssUtil ossUtil ;

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

    @Override
    public void deleteByUserAuthIdAndType(Integer userAuthId, Integer fileType) {
        List<UserInfoAuthFile> userInfoAuthFiles = findByUserAuthIdAndType(userAuthId,fileType);
        String[] urls = new String[userInfoAuthFiles.size()];
        for(int i=0;i<userInfoAuthFiles.size();i++){
            urls[i] = userInfoAuthFiles.get(i).getUrl();
        }
        userInfoAuthFileDao.deleteByUserAuthIdAndType(userAuthId,fileType);
        ossUtil.deleteFile(urls);
    }

    public int deleteFile(UserInfoAuthFile userInfoAuthFile){
        ossUtil.deleteFile(userInfoAuthFile.getUrl());
        return deleteById(userInfoAuthFile.getId());
    }



}
