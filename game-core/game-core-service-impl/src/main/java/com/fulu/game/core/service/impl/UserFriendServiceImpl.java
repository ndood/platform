package com.fulu.game.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.dao.UserFriendDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserFriend;
import com.fulu.game.core.entity.vo.UserFriendVO;
import com.fulu.game.core.service.UserFriendService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/8/27 11:08.
 * @Description: 用户好友
 */
@Service("userFriendService")
@Slf4j
public class UserFriendServiceImpl extends AbsCommonService<UserFriend, Integer> implements UserFriendService {
    @Autowired
    private UserFriendDao userFriendDao;

    @Autowired
    private UserService userService;

    @Override
    public ICommonDao<UserFriend, Integer> getDao() {
        return userFriendDao;
    }

    @Override
    public JSONObject save(UserFriendVO userFriendVO) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",1);
        jsonObject.put("msg","成功");
        if(userFriendVO == null){
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        User user = userService.getCurrentUser();
        userFriendVO.setFromUserId(user.getId());
        UserFriend friend = userFriendDao.findByFromAndToUserId(userFriendVO);
        userFriendVO.setStatus(1);
        if(friend != null){
            userFriendVO.setUpdateTime(new Date());
            userFriendVO.setId(friend.getId());
            userFriendDao.update(userFriendVO);
        } else {
            userFriendVO.setCreateTime(new Date());
            userFriendVO.setUpdateTime(new Date());
            if(userFriendVO.getIsAttention() == null){
                userFriendVO.setIsAttention(0);
            }
            if(userFriendVO.getIsBlack() == null){
                userFriendVO.setIsBlack(0);
            }
            userFriendDao.create(userFriendVO);
        }
        jsonObject.put("data", userFriendVO);
        return jsonObject;
    }

    /**
     * 获取关注人列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<UserFriendVO> getAttentions(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        User user = userService.getCurrentUser();
        UserFriendVO userFriendVO = new UserFriendVO();
        userFriendVO.setIsAttention(1);
        userFriendVO.setIsBlack(0);
        userFriendVO.setFromUserId(user.getId());
        userFriendVO.setType(1);
        List<UserFriendVO> list = userFriendDao.findByParameter(userFriendVO);
        return new PageInfo<>(list);
    }

    /**
     * 获取粉丝列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<UserFriendVO> getFans(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        User user = userService.getCurrentUser();
        UserFriendVO userFriendVO = new UserFriendVO();
        userFriendVO.setIsAttention(1);
        userFriendVO.setIsBlack(0);
        userFriendVO.setToUserId(user.getId());
        userFriendVO.setType(2);
        List<UserFriendVO> list = userFriendDao.findByParameter(userFriendVO);
        return new PageInfo<>(list);
    }

    /**
     * 获取黑名单列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<UserFriendVO> getBlacks(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        User user = userService.getCurrentUser();
        UserFriendVO userFriendVO = new UserFriendVO();
        userFriendVO.setIsBlack(1);
        userFriendVO.setFromUserId(user.getId());
        userFriendVO.setType(1);
        List<UserFriendVO> list = userFriendDao.findByParameter(userFriendVO);
        return new PageInfo<>(list);
    }

    /**
     * 是否是黑名单用户
     *
     * @param toUserId
     * @return
     */
    @Override
    public JSONObject isBlackUser(Integer toUserId) {
        JSONObject jsonObject = new JSONObject();
        User user = userService.getCurrentUser();
        jsonObject.put("isBlack",0);
        jsonObject.put("msg","非黑名单用户");
        jsonObject.put("data",new JSONObject());
        UserFriendVO userFriendVO = new UserFriendVO();
        userFriendVO.setIsBlack(1);
        userFriendVO.setFromUserId(user.getId());
        userFriendVO.setToUserId(toUserId);
        userFriendVO.setType(1);
        List<UserFriendVO> list = userFriendDao.findByParameter(userFriendVO);
        if(list != null && list.size() > 0){
            jsonObject.put("isBlack",1);
            jsonObject.put("msg","黑名单用户");
            jsonObject.put("data",list.get(0));
        }
        return jsonObject;
    }

    /**
     * 查询好友列表（包含关注人和粉丝）
     * @param pageNum
     * @param pageSize
     * @param keyWord
     * @return
     */
    @Override
    public PageInfo<UserFriendVO> searchFriends(Integer pageNum, Integer pageSize, String keyWord) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        User user = userService.getCurrentUser();
        UserFriendVO userFriendVO = new UserFriendVO();
        userFriendVO.setUserId(user.getId());
        userFriendVO.setNickname(keyWord);
        List<UserFriendVO> list = userFriendDao.searchFriends(userFriendVO);
        return new PageInfo<>(list);
    }

    /**
     * 查询用户列表
     * @param pageNum
     * @param pageSize
     * @param keyWord
     * @return
     */
    @Override
    public PageInfo<UserFriendVO> searchUsers(Integer pageNum, Integer pageSize, String keyWord) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        User user = userService.getCurrentUser();
        UserFriendVO userFriendVO = new UserFriendVO();
        userFriendVO.setUserId(user.getId());
        userFriendVO.setNickname(keyWord);
        List<UserFriendVO> list = userFriendDao.searchUsers(userFriendVO);
        return new PageInfo<>(list);
    }
}
