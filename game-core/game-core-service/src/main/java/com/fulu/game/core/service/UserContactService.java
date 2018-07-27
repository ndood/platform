package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserContact;
import com.fulu.game.core.entity.vo.UserContactVO;


/**
 * @author wangbin
 * @email ${email}
 * @date 2018-07-24 19:35:43
 */
public interface UserContactService extends ICommonService<UserContact, Integer> {


    UserContact save(int userId, int type, String contact);

    /**
     * 保存用户联系方式
     *
     * @param userId    用户id
     * @param type      联系方式类型
     * @param contact   具体联系号码
     * @param isDefault 是否为默认联系方式
     * @return 用户联系方式Bean
     */
    UserContact save(int userId, int type, String contact, boolean isDefault);


    void activeDefault(int userId, int id);

    /**
     * 保存用户联系方式
     *
     * @param userContactVO 参数VO
     * @return 用户联系方式Bean
     */
    UserContact saveUserContact(UserContactVO userContactVO);
}
