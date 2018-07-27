package com.fulu.game.core.service.impl;


import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.ContactTypeEnum;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserContactDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserContact;
import com.fulu.game.core.entity.vo.UserContactVO;
import com.fulu.game.core.service.UserContactService;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
@Slf4j
public class UserContactServiceImpl extends AbsCommonService<UserContact, Integer> implements UserContactService {

    private final UserContactDao userContactDao;
    private final UserService userService;

    @Autowired
    public UserContactServiceImpl(UserContactDao userContactDao, UserService userService) {
        this.userContactDao = userContactDao;
        this.userService = userService;
    }

    @Override
    public ICommonDao<UserContact, Integer> getDao() {
        return userContactDao;
    }


    @Override
    public UserContact save(int userId, int type, String contact) {
        UserContact userContacts = userContactDao.findByUserIdAndType(userId, type);
        if (userContacts == null) {
            userContacts = new UserContact();
            userContacts.setUserId(userId);
            userContacts.setType(type);
            userContacts.setIsDefault(false);
            userContacts.setContact(contact);
            userContacts.setCreateTime(new Date());
            create(userContacts);
        } else {
            if (!userContacts.getContact().equals(contact)) {
                userContacts.setContact(contact);
                userContacts.setUpdateTime(new Date());
                update(userContacts);
            }
        }
        return userContacts;
    }

    @Override
    public UserContact save(int userId, int type, String contact, boolean isDefault) {
        UserContact userContacts = userContactDao.findByUserIdAndType(userId, type);
        if (userContacts == null) {
            userContacts = new UserContact();
            userContacts.setUserId(userId);
            userContacts.setType(type);
            userContacts.setIsDefault(isDefault);
            userContacts.setContact(contact);
            userContacts.setCreateTime(new Date());
            create(userContacts);
        } else {
            if (!userContacts.getContact().equals(contact)) {
                userContacts.setContact(contact);
                userContacts.setUpdateTime(new Date());
                update(userContacts);
            }
        }
        return userContacts;
    }

    @Override
    public UserContact saveUserContact(UserContactVO userContactVO) {
        if (userContactVO == null) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }

        User user = userService.getCurrentUser();
        if (user == null) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        log.info("保存陪玩师userId:{}联系方式", user.getId());

        String wechat = userContactVO.getWechat();
        String qq = userContactVO.getQq();
        String mobile = userContactVO.getMobile();
        Integer defaultType = userContactVO.getDefaultType();
        if (StringUtils.isNotBlank(wechat)) {
            return save(user.getId(), ContactTypeEnum.WE_CHAT.getType(), wechat,
                    defaultType.equals(ContactTypeEnum.WE_CHAT.getType()));
        } else if (StringUtils.isNotBlank(qq)) {
            return save(user.getId(), ContactTypeEnum.QQ.getType(), wechat,
                    defaultType.equals(ContactTypeEnum.QQ.getType()));
        } else if (StringUtils.isNotBlank(mobile)) {
            if(!Constant.MOBILE_NUMBER_LENGTH.equals(mobile.length())) {
                log.error("陪玩师userId:{}的手机号mobile:{}非法", user.getId(), mobile);
                return null;
            }
            return save(user.getId(), ContactTypeEnum.MOBILE.getType(), wechat,
                    defaultType.equals(ContactTypeEnum.MOBILE.getType()));
        }
        return null;
    }

    @Override
    public void activeDefault(int userId, int id) {
        userContactDao.updateOtherDefault(userId, new Date());
        UserContact userContacts = new UserContact();
        userContacts.setId(id);
        userContacts.setIsDefault(true);
        userContacts.setUpdateTime(new Date());
        update(userContacts);
    }


}
