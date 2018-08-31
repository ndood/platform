package com.fulu.game.core.dao;

import com.fulu.game.core.entity.UserFriend;
import com.fulu.game.core.entity.vo.UserFriendVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/8/27 11:05.
 * @Description: 用户朋友
 */
@Mapper
public interface UserFriendDao extends ICommonDao<UserFriend,Integer>{

    /**
     * 通过作者和目标用户id获取朋友信息
     * @param userFriendVO
     * @return
     */
    public UserFriend findByFromAndToUserId(UserFriendVO userFriendVO);

    public List<UserFriendVO> findByParameter(UserFriendVO userFriendVO);

    /**
     * 查询好友列表（包含关注人和粉丝）
     * @param userFriendVO
     * @return
     */
    public List<UserFriendVO> searchFriends(UserFriendVO userFriendVO);

    /**
     * 查询用户列表
     * @param userFriendVO
     * @return
     */
    public List<UserFriendVO> searchUsers(UserFriendVO userFriendVO);

    /**
     * 获取所有关注
     * @param id
     * @return
     */
    public List<UserFriend> getAllAttentionsByUserId(Integer id);
}
