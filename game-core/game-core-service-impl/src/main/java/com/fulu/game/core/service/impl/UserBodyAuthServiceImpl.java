package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.UserBodyAuthStatusEnum;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserBodyAuthDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.UserBodyAuth;
import com.fulu.game.core.entity.vo.UserBodyAuthVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.UserBodyAuthService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class UserBodyAuthServiceImpl extends AbsCommonService<UserBodyAuth, Integer> implements UserBodyAuthService {

    @Autowired
    private UserBodyAuthDao userBodyAuthDao;
    @Autowired
    private AdminService adminService;

    @Override
    public ICommonDao<UserBodyAuth, Integer> getDao() {
        return userBodyAuthDao;
    }

    @Override
    public List<UserBodyAuth> findByParameter(UserBodyAuthVO userBodyAuthVO) {
        return userBodyAuthDao.findByParameter(userBodyAuthVO);
    }

    @Override
    public void submitUserBodyAuthInfo(UserBodyAuthVO userBodyAuthVO) {

        //判断用户是否已提交认证信息
        UserBodyAuthVO param = new UserBodyAuthVO();
        param.setUserId(userBodyAuthVO.getUserId());

        List<UserBodyAuth> resultList = userBodyAuthDao.findByParameter(param);


        if (CollectionUtils.isNotEmpty(resultList)) {

            UserBodyAuth resultUba = resultList.get(0);
            //判断用户是否是需要认证的
            if (resultUba.getAuthStatus().intValue() == UserBodyAuthStatusEnum.AUTH_SUCCESS.getType().intValue()) {
                log.error("提交认证异常，当前操作用户已经通过了身份认证，不可再次提交认证");
                throw new UserException(UserException.ExceptionCode.BODY_ALREADY_AUTH);
            }

            //删除旧的认证信息
            userBodyAuthDao.deleteById(resultUba.getId());
        }

        userBodyAuthDao.create(userBodyAuthVO);
    }

    @Override
    public boolean pass(Integer userId) {
        Admin admin = adminService.getCurrentUser();

        UserBodyAuth auth = userBodyAuthDao.findByUserId(userId);
        if (auth == null) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        auth.setAuthStatus(UserBodyAuthStatusEnum.AUTH_SUCCESS.getType());
        auth.setAdminId(admin.getId());
        auth.setAdminName(admin.getName());
        auth.setUpdateTime(DateUtil.date());

        return update(auth) > 0;
    }

    @Override
    public boolean reject(Integer userId, String remark) {
        Admin admin = adminService.getCurrentUser();

        UserBodyAuth auth = userBodyAuthDao.findByUserId(userId);
        auth.setAuthStatus(UserBodyAuthStatusEnum.AUTH_FAIL.getType());
        auth.setAdminId(admin.getId());
        auth.setAdminName(admin.getName());
        if (StringUtils.isNotBlank(remark)) {
            auth.setRemarks(remark);
        }
        auth.setUpdateTime(DateUtil.date());

        return update(auth) > 0;
    }

    @Override
    public PageInfo<UserBodyAuthVO> findByVO(Integer pageNum, Integer pageSize, UserBodyAuthVO userBodyAuthVO) {
        PageHelper.startPage(pageNum, pageSize, " t1.create_time desc");

        List<UserBodyAuthVO> list = list(userBodyAuthVO);

        return new PageInfo<UserBodyAuthVO>(list);
    }

    @Override
    public List<UserBodyAuthVO> list(UserBodyAuthVO userBodyAuthVO) {
        return userBodyAuthDao.findByVO(userBodyAuthVO);
    }


    @Override
    public UserBodyAuth findByUserId(Integer userId) {
        return userBodyAuthDao.findByUserId(userId);
    }


    @Override
    public boolean userAlreadyAuth(Integer userId) {
        UserBodyAuth uba = userBodyAuthDao.findByUserId(userId);
        if(uba == null) {
            log.info("当前用户id={}未进行身份验证", userId);
            throw new UserException(UserException.ExceptionCode.BODY_NO_AUTH);
        }

        if(uba.getAuthStatus().intValue() == UserBodyAuthStatusEnum.AUTH_SUCCESS.getType().intValue()){
            return true;
        }
        return false;
    }
}
