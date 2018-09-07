package com.fulu.game.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.ParamsException;
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

    @Autowired
    private RedisOpenServiceImpl redisOpenService;

    @Override
    public ICommonDao<UserFriend, Integer> getDao() {
        return userFriendDao;
    }

    @Override
    public void save(UserFriendVO userFriendVO) {
        if(userFriendVO == null){
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        User user = userService.getCurrentUser();
        userFriendVO.setFromUserId(user.getId());
        // 设置关注的Redis信息，必须有fromUserId和toUserId
        saveAttentionInfo(userFriendVO);
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

    }

    /**
     * 保存关注信息
     * @param userFriendVO
     */
    private void saveAttentionInfo(UserFriendVO userFriendVO){
        if(userFriendVO == null || userFriendVO.getFromUserId() == null || userFriendVO.getToUserId() == null
                || userFriendVO.getIsAttention() == null){
            return ;
        }
        Integer fromUserId = userFriendVO.getFromUserId();
        Integer toUserId = userFriendVO.getToUserId();
        boolean isAttention = userFriendVO.getIsAttention().intValue() == 0 ? false : true;
        // 保存用户关注那些用户信息
        redisOpenService.bitSet(RedisKeyEnum.ATTENTION_USERS.generateKey(fromUserId),toUserId,isAttention);
        // 保存用户被那些用户关注
        redisOpenService.bitSet(RedisKeyEnum.ATTENTIONED_USERS.generateKey(toUserId),fromUserId,isAttention);
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
        log.info("检测空指针：userFriendVO.toJSONString: {}, pageNum: {}, pageSize: {}",JSONObject.toJSONString(userFriendVO),pageNum,pageSize);
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
        log.info("检测空指针：userFriendVO.toJSONString: {}, pageNum: {}, pageSize: {}",JSONObject.toJSONString(userFriendVO),pageNum,pageSize);
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
        log.info("检测空指针：userFriendVO.toJSONString: {}, pageNum: {}, pageSize: {}",JSONObject.toJSONString(userFriendVO),pageNum,pageSize);
        List<UserFriendVO> list = userFriendDao.findByParameter(userFriendVO);
        return new PageInfo<>(list);
    }

    /**
     * 是否是黑名单用户
     *
     * @param toUserId
     * @return 1:是；0：不是；
     */
    @Override
    public boolean isBlackUser(Integer toUserId) {
        User user = userService.getCurrentUser();
        boolean isBlack = false;
        UserFriendVO userFriendVO = new UserFriendVO();
        userFriendVO.setIsBlack(1);
        userFriendVO.setFromUserId(user.getId());
        userFriendVO.setToUserId(toUserId);
        userFriendVO.setType(1);
        log.info("检测空指针：userFriendVO.toJSONString: {}",JSONObject.toJSONString(userFriendVO));
        List<UserFriendVO> list = userFriendDao.findByParameter(userFriendVO);
        if(list != null && list.size() > 0){
            isBlack = true;
        }
        return isBlack;
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
        log.info("检测空指针：userFriendVO.toJSONString: {}, pageNum: {}, pageSize: {}",JSONObject.toJSONString(userFriendVO),pageNum,pageSize);
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
        log.info("检测空指针：userFriendVO.toJSONString: {}, pageNum: {}, pageSize: {}",JSONObject.toJSONString(userFriendVO),pageNum,pageSize);
        List<UserFriendVO> list = userFriendDao.searchUsers(userFriendVO);
        return new PageInfo<>(list);
    }

    /**
     * 获取所有关注人信息
     *
     * @param id 用户id
     * @return
     */
    @Override
    public List<UserFriend> getAllAttentionsByUserId(Integer id) {
        return userFriendDao.getAllAttentionsByUserId(id);
    }
}
