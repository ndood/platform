package com.fulu.game.core.service.impl;

import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.AuthStatusEnum;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.UserTypeEnum;
import com.fulu.game.common.enums.exception.UserExceptionEnums;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.util.BeanUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("userService")
public class UserServiceImpl extends AbsCommonService<User, Integer> implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;


    @Override
    public ICommonDao<User, Integer> getDao() {
        return userDao;
    }

    @Override
    public User findByMobile(String mobile) {
        UserVO userVO = new UserVO();
        userVO.setMobile(mobile);
        List<User> users = userDao.findByParameter(userVO);
        if (CollectionUtil.isEmpty(users)) {
            return null;
        }
        return users.get(0);
    }

    @Override
    public User findByOpenId(String openId) {
        UserVO userVO = new UserVO();
        userVO.setOpenId(openId);
        List<User> users = userDao.findByParameter(userVO);
        if (CollectionUtil.isEmpty(users)) {
            return null;
        }
        return users.get(0);
    }

    @Override
    public User findByImId(String imId) {
        UserVO userVO = new UserVO();
        userVO.setImId(imId);
        List<User> userList = userDao.findByParameter(userVO);
        if (CollectionUtil.isEmpty(userList)) {
            return null;
        }
        return userList.get(0);
    }

    @Override
    public List<User> findByImIds(String imIds) {
        String[] imIdArr = imIds.split(Constant.DEFAULT_SPLIT_SEPARATOR);
        List<User> userList = new ArrayList<User>();
        UserVO userVO = new UserVO();
        if (imIdArr.length > 0) {
            for (int i = 0; i < imIdArr.length; i++) {
                userVO.setImId(imIdArr[i]);
                List<User> users = userDao.findByParameter(userVO);
                if (users.size() > 0) {
                    User user = users.get(0);
                    userList.add(user);
                }
            }
        }
        return userList;
    }


    @Override
    public void lock(int id) {
        User user = findById(id);
        user.setStatus(0);
        userDao.update(user);
    }

    @Override
    public void unlock(int id) {
        User user = findById(id);
        user.setStatus(1);
        userDao.update(user);
        SubjectUtil.setCurrentUser(user);
    }

    @Override
    public PageInfo<User> list(UserVO userVO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, userVO.getOrderBy());
        List<User> list = userDao.findByParameter(userVO);
        return new PageInfo(list);
    }

    @Override
    public User save(UserVO userVO) {
        User user = new User();
        BeanUtil.copyProperties(userVO, user);
        user.setStatus(1);//默认账户解封状态
        user.setType(UserTypeEnum.GENERAL_USER.getType());//默认普通用户
        user.setUserInfoAuth(AuthStatusEnum.NOT_PERFECT.getType());//默认未审核
        user.setBalance(Constant.DEFAULT_BALANCE);
        user.setScoreAvg(Constant.DEFAULT_SCORE_AVG);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userDao.create(user);
        SubjectUtil.setCurrentUser(user);
        return user;
    }

    @Override
    public User getCurrentUser() {
        Object userObj = SubjectUtil.getCurrentUser();
        if (null == userObj) {
            throw new UserException(UserExceptionEnums.USER_NOT_EXIST_EXCEPTION);
        }
        if (userObj instanceof User) {
            return (User) userObj;
        } else {
            return null;
        }
    }


    public void updateRedisUser(User user) {
        String token = SubjectUtil.getToken();
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap = BeanUtil.beanToMap(user);
        redisOpenService.hset(RedisKeyEnum.PLAY_TOKEN.generateKey(token), userMap);
    }

    @Override
    public Boolean isCurrentUser(Integer userId) {
        User currentUser = getCurrentUser();
        if (currentUser.getId().equals(userId)) {
            return true;
        }
        throw new ServiceErrorException("用户不匹配!");
    }

}
