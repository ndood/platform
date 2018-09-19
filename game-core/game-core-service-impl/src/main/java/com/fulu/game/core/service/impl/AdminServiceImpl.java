package com.fulu.game.core.service.impl;

import com.fulu.game.common.domain.Password;
import com.fulu.game.common.enums.AdminStatus;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.EncryptUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.dao.AdminDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.vo.AdminVO;
import com.fulu.game.core.service.AdminRoleService;
import com.fulu.game.core.service.AdminService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("adminService")
@Slf4j
public class AdminServiceImpl extends AbsCommonService<Admin, Integer> implements AdminService {

    @Autowired
    private AdminDao adminDao;

    @Autowired
    private AdminRoleService adminRoleService;

    @Override
    public ICommonDao<Admin, Integer> getDao() {
        return adminDao;
    }

    @Override
    public Admin findByUsername(String username) {
        AdminVO memberVO = new AdminVO();
        memberVO.setUsername(username);
        List<Admin> adminList = adminDao.findByParameter(memberVO);
        if (adminList.isEmpty()) {
            return null;
        }
        return adminList.get(0);
    }

    @Override
    public Admin save(AdminVO adminVO) {
        if(adminVO != null && (adminVO.getRoleId() == null || adminVO.getRoleId().intValue() <= 0)){
            throw new UserException(UserException.ExceptionCode.NO_ROLE);
        }
        //判断username是否重名
        AdminVO requestVO = new AdminVO();
        requestVO.setName(adminVO.getName());
        List<Admin> adminList = adminDao.findByParameter(requestVO);
        if (!CollectionUtil.isEmpty(adminList)) {
            throw new UserException(UserException.ExceptionCode.NAME_DUMPLICATE_EXCEPTION);
        } else {
            requestVO.setName(null);
            requestVO.setUsername(adminVO.getUsername());
            List<Admin> adminList1 = adminDao.findByParameter(requestVO);
            if (!CollectionUtil.isEmpty(adminList1)) {
                throw new UserException(UserException.ExceptionCode.USERNAME_DUMPLICATE_EXCEPTION);
            }
        }

        Admin admin = new Admin();
        admin.setName(adminVO.getName());
        admin.setUsername(adminVO.getUsername());
        admin.setStatus(AdminStatus.ENABLE.getType());
        Password password = EncryptUtil.PiecesEncode(adminVO.getPassword());
        admin.setPassword(password.getPassword());
        admin.setSalt(password.getSalt());
        admin.setCreateTime(new Date());
        admin.setUpdateTime(admin.getCreateTime());
        if(adminVO.getId() != null && admin.getId().intValue() > 0){
            admin.setId(adminVO.getId());
            //登录名不允许修改
            admin.setUsername(null);
            adminDao.update(admin);
        } else {
            adminDao.create(admin);
        }
        //设置用户角色信息
        adminRoleService.save(admin);
        return admin;
    }

    @Override
    public PageInfo<Admin> list(AdminVO adminVO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "create_time DESC");
        List<Admin> list = adminDao.findByParameter(adminVO);
        return new PageInfo(list);
    }

    @Override
    public void lock(Integer adminId) {
        Admin currentAdmin = getCurrentUser();
        log.info("调用禁用管理员接口，操作人adminId={},禁用对象id={}", currentAdmin.getId(), adminId);
        if (AdminStatus.DISABLE.getType().equals(currentAdmin.getStatus())) {
            throw new UserException(UserException.ExceptionCode.LOCK_DENY_EXCEPTION);
        }
        Admin admin = adminDao.findById(adminId);
        if (admin == null){
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        if (currentAdmin.getId().equals(admin.getId())) {
            throw new UserException(UserException.ExceptionCode.LOCK_SELF_EXCEPTION);
        }
        admin.setStatus(AdminStatus.DISABLE.getType());
        admin.setUpdateTime(new Date());
        adminDao.update(admin);
    }

    @Override
    public Admin getCurrentUser() {
        Object userObj = SubjectUtil.getCurrentUser();
        if (null == userObj) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        if (userObj instanceof Admin) {
            return (Admin) userObj;
        } else {
            return null;
        }
    }

}
