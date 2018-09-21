package com.fulu.game.admin.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.fulu.game.admin.service.AdminUserInfoAuthService;
import com.fulu.game.common.enums.FileTypeEnum;
import com.fulu.game.common.enums.UserInfoAuthStatusEnum;
import com.fulu.game.common.enums.UserTypeEnum;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.exception.UserAuthException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserInfoAuthDao;
import com.fulu.game.core.dao.UserInfoAuthFileTempDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.UserInfoAuthFileTempVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.ProductService;
import com.fulu.game.core.service.UserInfoAuthRejectService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.UserInfoAuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class AdminUserInfoAuthServiceImpl extends UserInfoAuthServiceImpl implements AdminUserInfoAuthService {

    @Autowired
    private UserInfoAuthDao userInfoAuthDao;
    @Autowired
    private UserInfoAuthRejectService userInfoAuthRejectService;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminUserTechAuthServiceImpl userTechAuthServiceImpl;
    @Autowired
    private AdminService adminService;
    @Autowired
    private ProductService productService;

    @Autowired
    private AdminPushServiceImpl adminPushService;
    @Autowired
    private UserInfoAuthFileTempDao userInfoAuthFileTempDao;

    @Override
    public ICommonDao<UserInfoAuth, Integer> getDao() {
        return userInfoAuthDao;
    }


    /**
     * 认证信息驳回
     *
     * @param id
     * @param reason
     * @return
     */
    @Override
    public UserInfoAuth reject(int id, String reason) {
        Admin admin = adminService.getCurrentUser();
        log.info("驳回用户个人认证信息:adminId:{};adminName:{};authInfoId:{},reason:{}", admin.getId(), admin.getName(), id, reason);
        //修改认证驳回状态
        UserInfoAuth userInfoAuth = findById(id);
        if (userInfoAuth == null) {
            throw new UserAuthException(UserAuthException.ExceptionCode.NOT_EXIST_USER_AUTH);
        }

        userInfoAuth.setIsRejectSubmit(true);
        update(userInfoAuth);
        //修改用户表认证状态信息
        User user = userService.findById(userInfoAuth.getUserId());
        //如果是用户冻结状态给错误提示
        if (user.getUserInfoAuth().equals(UserInfoAuthStatusEnum.FREEZE.getType())) {
            throw new UserAuthException(UserAuthException.ExceptionCode.SERVICE_USER_FREEZE_ADMIN);
        }
        user.setUserInfoAuth(UserInfoAuthStatusEnum.NOT_PERFECT.getType());
        user.setUpdateTime(new Date());
        userService.update(user);
        //添加驳回理由
        UserInfoAuthReject userInfoAuthReject = new UserInfoAuthReject();
        userInfoAuthReject.setReason(reason);
        userInfoAuthReject.setUserInfoAuthStatus(user.getUserInfoAuth());
        userInfoAuthReject.setUserId(userInfoAuth.getUserId());
        userInfoAuthReject.setAdminId(admin.getId());
        userInfoAuthReject.setAdminName(admin.getName());
        userInfoAuthReject.setUserInfoAuthId(id);
        userInfoAuthReject.setCreateTime(new Date());
        userInfoAuthRejectService.create(userInfoAuthReject);
        //同步下架用户该技能商品
//        productService.disabledProductByUser(userInfoAuth.getUserId());
        adminPushService.userInfoAuthFail(user.getId(), reason);
        return userInfoAuth;
    }

    /**
     * 技能审核通过
     *
     * @param id
     * @return
     */
    @Override
    public UserInfoAuth pass(int id) {
        Admin admin = adminService.getCurrentUser();
        log.info("清除用户认证信息驳回状态:adminId:{};adminName:{};authInfoId:{}", admin.getId(), admin.getName(), id);
        UserInfoAuth userInfoAuth = findById(id);
        userInfoAuth.setIsRejectSubmit(false);
        update(userInfoAuth);
        //修改用户表认证状态信息
        User user = userService.findById(userInfoAuth.getUserId());
        //如果是用户冻结状态给错误提示
        if (user.getUserInfoAuth().equals(UserInfoAuthStatusEnum.FREEZE.getType())) {
            throw new UserAuthException(UserAuthException.ExceptionCode.SERVICE_USER_FREEZE_ADMIN);
        }
        user.setUserInfoAuth(UserInfoAuthStatusEnum.VERIFIED.getType());
        user.setType(UserTypeEnum.ACCOMPANY_PLAYER.getType());
        userService.update(user);

        Integer userId = userInfoAuth.getUserId();
        UserInfoAuthFileTempVO tempVO = new UserInfoAuthFileTempVO();
        tempVO.setUserId(userId);
        List<UserInfoAuthFileTemp> fileTempList = userInfoAuthFileTempDao.findByParameter(tempVO);
        if (CollectionUtil.isNotEmpty(fileTempList)) {
            //更新主图
            for (UserInfoAuthFileTemp fileTemp : fileTempList) {
                Integer type = fileTemp.getType();
                if (type.equals(FileTypeEnum.MAIN_PIC.getType())) {
                    UserInfoAuth infoAuth = new UserInfoAuth();
                    infoAuth.setId(id);
                    infoAuth.setUserId(userId);
                    infoAuth.setMainPicUrl(fileTemp.getUrl());
                    userInfoAuthDao.update(infoAuth);
                }
            }
            deleteTempFileAndUpdateOrg(userId);
            userInfoAuthFileTempDao.deleteByUserId(userId);
        }

        //给用户推送通知
        adminPushService.userInfoAuthSuccess(userId);
        return userInfoAuth;
    }


    /**
     * 认证信息冻结
     *
     * @param id
     * @param reason
     * @return
     */
    @Override
    public UserInfoAuth freeze(int id, String reason) {
        Admin admin = adminService.getCurrentUser();
        log.info("冻结用户个人认证信息:adminId:{};adminName:{};authInfoId:{},reason:{}", admin.getId(), admin.getName(), id, reason);
        UserInfoAuth userInfoAuth = findById(id);
        if (userInfoAuth == null) {
            throw new UserAuthException(UserAuthException.ExceptionCode.NOT_EXIST_USER_AUTH);
        }
        //修改用户表认证状态信息
        User user = userService.findById(userInfoAuth.getUserId());
        user.setUserInfoAuth(UserInfoAuthStatusEnum.FREEZE.getType());
        user.setUpdateTime(new Date());
        userService.update(user);

        //添加驳回理由
        UserInfoAuthReject userInfoAuthReject = new UserInfoAuthReject();
        userInfoAuthReject.setReason(reason);
        userInfoAuthReject.setUserInfoAuthStatus(user.getUserInfoAuth());
        userInfoAuthReject.setUserId(userInfoAuth.getUserId());
        userInfoAuthReject.setUserInfoAuthId(id);
        userInfoAuthReject.setAdminId(admin.getId());
        userInfoAuthReject.setAdminName(admin.getName());
        userInfoAuthReject.setCreateTime(new Date());
        userInfoAuthRejectService.create(userInfoAuthReject);

        //下架该用户上传的所有商品
        productService.disabledProductByUser(userInfoAuth.getUserId());
        return userInfoAuth;
    }

    @Override
    public UserInfoAuth unFreeze(int id) {
        Admin admin = adminService.getCurrentUser();
        log.info("解冻用户个人认证信息:adminId:{};adminName:{};authInfoId:{};", admin.getId(), admin.getName(), id);
        UserInfoAuth userInfoAuth = findById(id);
        //修改用户表认证状态信息
        User user = userService.findById(userInfoAuth.getUserId());
        user.setUserInfoAuth(UserInfoAuthStatusEnum.VERIFIED.getType());
        user.setUpdateTime(new Date());
        userService.update(user);
        //同步恢复用户正确技能的商品状态
        List<UserTechAuth> userTechAuthList = userTechAuthServiceImpl.findUserNormalTechs(userInfoAuth.getUserId());
        for (UserTechAuth userTechAuth : userTechAuthList) {
            productService.recoverProductActivateByTechAuthId(userTechAuth.getId());
        }

        return userInfoAuth;
    }

    @Override
    public UserInfoAuth setVest(Integer id) {
        UserInfoAuth userInfoAuth = userInfoAuthDao.findById(id);
        //没有认证技能的情况，抛出提示
        List<UserTechAuth> authList = userTechAuthServiceImpl.findUserNormalTechs(userInfoAuth.getUserId());
        if (CollectionUtils.isEmpty(authList)) {
            throw new ServiceErrorException("该用户没有可用的技能！");
        }

        Boolean vestFlag = userInfoAuth.getVestFlag() == null ? false : userInfoAuth.getVestFlag();
        if (vestFlag) {
            productService.stopOrderReceiving(userInfoAuth.getUserId());
        } else {
            productService.startOrderReceiving(24 * 30F, userInfoAuth.getUserId());
        }
        userInfoAuth.setVestFlag(!vestFlag);
        userInfoAuthDao.update(userInfoAuth);
        return userInfoAuth;
    }
}
