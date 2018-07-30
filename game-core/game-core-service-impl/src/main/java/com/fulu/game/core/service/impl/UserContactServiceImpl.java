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
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


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
    public boolean saveUserContact(UserContactVO userContactVO) {
        boolean flag = false;
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

        if (StringUtils.isBlank(wechat) && StringUtils.isBlank(qq) && StringUtils.isBlank(mobile)) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }

        if (StringUtils.isNotBlank(wechat)) {
            save(user.getId(), ContactTypeEnum.WE_CHAT.getType(), wechat,
                    defaultType.equals(ContactTypeEnum.WE_CHAT.getType()));
            flag = true;
        }
        if (StringUtils.isNotBlank(qq)) {
            save(user.getId(), ContactTypeEnum.QQ.getType(), qq,
                    defaultType.equals(ContactTypeEnum.QQ.getType()));
            flag = true;
        }
        if (StringUtils.isNotBlank(mobile)) {
            if (!Constant.MOBILE_NUMBER_LENGTH.equals(mobile.length())) {
                log.error("陪玩师userId:{}的手机号mobile:{}非法", user.getId(), mobile);
                throw new UserException(UserException.ExceptionCode.IllEGAL_MOBILE_EXCEPTION);
            }
            save(user.getId(), ContactTypeEnum.MOBILE.getType(), mobile,
                    defaultType.equals(ContactTypeEnum.MOBILE.getType()));
            flag = true;
        }
        return flag;
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

    @Override
    public UserContactVO getUserContact() {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        UserContactVO userContactVO = new UserContactVO();
        userContactVO.setUserId(user.getId());
        List<UserContact> userContactList = userContactDao.findByParameter(userContactVO);
        if (CollectionUtil.isEmpty(userContactList)) {
            return null;
        }

        UserContactVO resultVo = new UserContactVO();
        for (UserContact meta : userContactList) {
            if (meta.getType().equals(ContactTypeEnum.MOBILE.getType())) {
                resultVo.setMobile(meta.getContact());
                if (meta.getIsDefault()) {
                    resultVo.setDefaultType(ContactTypeEnum.MOBILE.getType());
                }
            }
            if (meta.getType().equals(ContactTypeEnum.QQ.getType())) {
                resultVo.setQq(meta.getContact());
                if (meta.getIsDefault()) {
                    resultVo.setDefaultType(ContactTypeEnum.QQ.getType());
                }
            }
            if (meta.getType().equals(ContactTypeEnum.WE_CHAT.getType())) {
                resultVo.setWechat(meta.getContact());
                if (meta.getIsDefault()) {
                    resultVo.setDefaultType(ContactTypeEnum.WE_CHAT.getType());
                }
            }
        }
        return resultVo;
    }
}
