package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserContact;



/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-24 19:35:43
 */
public interface UserContactService extends ICommonService<UserContact,Integer>{



     UserContact save(int userId,int type,String contact);


     void activeDefault(int userId, int id);
}
