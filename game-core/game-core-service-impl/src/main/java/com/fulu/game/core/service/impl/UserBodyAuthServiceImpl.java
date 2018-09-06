package com.fulu.game.core.service.impl;


import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.UserBodyAuthVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.UserBodyAuthDao;
import com.fulu.game.core.entity.UserBodyAuth;
import com.fulu.game.core.service.UserBodyAuthService;

import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class UserBodyAuthServiceImpl extends AbsCommonService<UserBodyAuth,Integer> implements UserBodyAuthService {

    @Autowired
	private UserBodyAuthDao userBodyAuthDao;



    @Override
    public ICommonDao<UserBodyAuth, Integer> getDao() {
        return userBodyAuthDao;
    }

    @Override
    public List<UserBodyAuth> findByParameter(UserBodyAuthVO userBodyAuthVO){
        return userBodyAuthDao.findByParameter(userBodyAuthVO);
    }

    @Override
    public void submitUserBodyAuthInfo(UserBodyAuthVO userBodyAuthVO) {
        
        //判断用户是否已提交认证信息
        UserBodyAuthVO param = new UserBodyAuthVO();
        param.setUserId(userBodyAuthVO.getUserId());

        List<UserBodyAuth> resultList = userBodyAuthDao.findByParameter(userBodyAuthVO);
        
        
        if(resultList !=null && resultList.size() >0){

            UserBodyAuth resultUba = resultList.get(0);
            //判断用户是否是需要认证的
            if(resultUba.getAuthStatus().intValue() == 1){
                log.error("提交认证异常，当前操作用户已经通过了身份认证，不可再次提交认证");
                throw new UserException(UserException.ExceptionCode.BODY_ALREADY_AUTH);
            }
            
            //删除旧的认证信息
            userBodyAuthDao.deleteById(resultUba.getId());
        }
        
        userBodyAuthDao.create(userBodyAuthVO);
    }
}
