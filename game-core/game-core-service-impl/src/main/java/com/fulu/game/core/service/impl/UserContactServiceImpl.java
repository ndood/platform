package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.UserContactVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.UserContactDao;
import com.fulu.game.core.entity.UserContact;
import com.fulu.game.core.service.UserContactService;

import java.util.Date;
import java.util.List;


@Service
public class UserContactServiceImpl extends AbsCommonService<UserContact,Integer> implements UserContactService {

    @Autowired
	private UserContactDao userContactDao;

    @Override
    public ICommonDao<UserContact, Integer> getDao() {
        return userContactDao;
    }


    @Override
    public UserContact save(int userId, int type, String contact) {
        UserContact userContacts = userContactDao.findByUserIdAndType(userId,type);
        if(userContacts==null){
            userContacts = new UserContact();
            userContacts.setUserId(userId);
            userContacts.setType(type);
            userContacts.setIsDefault(false);
            userContacts.setContact(contact);
            userContacts.setCreateTime(new Date());
            create(userContacts);
        }else{
            if(!userContacts.getContact().equals(contact)){
                userContacts.setContact(contact);
                userContacts.setUpdateTime(new Date());
                update(userContacts);
            }
        }
        return userContacts;
    }


    public void activeDefault(int userId, int id){
        userContactDao.updateOtherDefault(userId,new Date());
        UserContact userContacts =  new UserContact();
        userContacts.setId(id);
        userContacts.setIsDefault(true);
        userContacts.setUpdateTime(new Date());
        update(userContacts);
    }


}
