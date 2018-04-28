package com.fulu.game.core.service.impl;

import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.UserVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fulu.game.core.dao.UserDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.UserService;

@Service("userService")
public class UserServiceImpl extends AbsCommonService<User,Integer> implements UserService {

    @Autowired
	private UserDao userDao;

    @Override
    public ICommonDao<User, Integer> getDao() {
        return userDao;
    }

    @Override
    public User findByMobile(String mobile) {
        UserVO userVO = new UserVO();
        userVO.setMobile(mobile);
        List<User> users =userDao.findByParameter(userVO);
        if(users!=null&&users.size()>0){
           return users.get(0);
        }
        return null;
    }

    @Override
    public User findByOpenId(String openId){
        UserVO userVO = new UserVO();
        userVO.setOpenId(openId);
        List<User> users =userDao.findByParameter(userVO);
        return users.size()>0?users.get(0):null;
    }

    @Override
    public void lock(int id){
        User user = findById(id);
        user.setStatus(0);
        userDao.update(user);
    }

    @Override
    public void unlock(int id){
        User user = findById(id);
        user.setStatus(1);
        userDao.update(user);
    }

    @Override
    public PageInfo<User> list(UserVO userVO,Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize,userVO.getOrderBy());
        List<User> list = userDao.findByParameter(userVO);
        return new PageInfo(list);
    }

    @Override
    public User save(UserVO userVO) {
        User user = new User();
        BeanUtil.copyProperties(userVO, user);
        user.setStatus(1);//默认账户解封状态
        user.setType(1);//默认普通用户
        user.setUserInfoAuth(0);//默认未审核
        user.setBalance(new BigDecimal("0.00"));
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userDao.create(user);
        return user;
    }

}
