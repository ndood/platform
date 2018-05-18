package com.fulu.game.core.service.impl;

import com.fulu.game.common.domain.Password;
import com.fulu.game.common.enums.AdminStatus;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.EncryptUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.AdminVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import com.fulu.game.core.dao.AdminDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.service.AdminService;

@Service("adminService")
public class AdminServiceImpl extends AbsCommonService<Admin, Integer> implements AdminService {

    @Autowired
    private AdminDao adminDao;

    @Override
    public ICommonDao<Admin, Integer> getDao() {
        return adminDao;
    }

    @Override
    public Admin findByUsername(String username) {
        AdminVO memberVO = new AdminVO();
        memberVO.setUsername(username);
        return adminDao.findByParameter(memberVO).get(0);
    }

    @Override
    public Admin save(AdminVO adminVO) {
        Admin admin = new Admin();
        admin.setName(adminVO.getName());
        admin.setUsername(adminVO.getUsername());
        admin.setStatus(AdminStatus.ENABLE.getType());
        Password password = EncryptUtil.PiecesEncode(adminVO.getPassword());
        admin.setPassword(password.getPassword());
        admin.setSalt(password.getSalt());
        admin.setCreateTime(new Date());
        admin.setUpdateTime(admin.getCreateTime());
        adminDao.create(admin);
        return admin;
    }


    @Override
    public PageInfo<Admin> list(AdminVO adminVO, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize, adminVO.getOrderBy());
        List<Admin> list = adminDao.findByParameter(adminVO);
        return new PageInfo(list);
    }

    @Override
    public Admin getCurrentUser(){
        Object userObj = SubjectUtil.getCurrentUser();
        if (null == userObj){
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        if(userObj instanceof Admin){
            return (Admin) userObj;
        }else{
            return null;
        }
    }

}
