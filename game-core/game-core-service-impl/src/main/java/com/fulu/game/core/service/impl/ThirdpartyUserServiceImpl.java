package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.SourceIdEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.ThirdpartyUserVO;
import com.fulu.game.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.ThirdpartyUserDao;
import com.fulu.game.core.entity.ThirdpartyUser;
import com.fulu.game.core.service.ThirdpartyUserService;

import java.util.Date;
import java.util.List;


@Service
public class ThirdpartyUserServiceImpl extends AbsCommonService<ThirdpartyUser,Integer> implements ThirdpartyUserService {

    @Autowired
	private ThirdpartyUserDao thirdpartyUserDao;
    @Autowired
    private UserService userService;

    @Override
    public ICommonDao<ThirdpartyUser, Integer> getDao() {
        return thirdpartyUserDao;
    }

    @Override
    public ThirdpartyUser findByFqlOpenid(String fqlOpenId) {
        if(fqlOpenId==null){
            return null;
        }
        ThirdpartyUserVO param = new ThirdpartyUserVO();
        param.setFqlOpenid(fqlOpenId);
        List<ThirdpartyUser> list = thirdpartyUserDao.findByParameter(param);
        if(list.isEmpty()){
            return null;
        }
        return list.get(0);
    }

    @Override
    public ThirdpartyUser findByUserId(Integer userId) {
        ThirdpartyUserVO param = new ThirdpartyUserVO();
        param.setUserId(userId);
        List<ThirdpartyUser> list = thirdpartyUserDao.findByParameter(param);
        if(list.isEmpty()){
            return null;
        }
        return list.get(0);
    }

    @Override
    public ThirdpartyUser createFenqileUser(String fqlOpenId,String ip) {
        User user = userService.createThirdPartyUser(SourceIdEnum.FENQILE.getType(),ip);
        ThirdpartyUser thirdpartyUser = new ThirdpartyUser();
        thirdpartyUser.setFqlOpenid(fqlOpenId);
        thirdpartyUser.setUserId(user.getId());
        thirdpartyUser.setCreateTime(new Date());
        thirdpartyUser.setUpdateTime(new Date());
        create(thirdpartyUser);
        return thirdpartyUser;
    }
}
