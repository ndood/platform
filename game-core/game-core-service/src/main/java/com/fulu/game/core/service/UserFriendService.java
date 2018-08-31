package com.fulu.game.core.service;

import com.alibaba.fastjson.JSONObject;
import com.fulu.game.core.entity.UserFriend;
import com.fulu.game.core.entity.vo.UserFriendVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/8/27 11:07.
 * @Description: 用户好友
 */
public interface UserFriendService extends ICommonService<UserFriend,Integer> {

    /**
     * 保存好友信息
     * @param userFriendVO
     * @return
     */
    public void save(UserFriendVO userFriendVO);

    /**
     * 获取关注人列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<UserFriendVO> getAttentions(Integer pageNum, Integer pageSize);

    /**
     * 获取粉丝列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<UserFriendVO> getFans(Integer pageNum, Integer pageSize);

    /**
     * 获取黑名单列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<UserFriendVO> getBlacks(Integer pageNum, Integer pageSize);

    /**
     * 是否是黑名单用户
     * @param toUserId
     * @return
     */
    public boolean isBlackUser(Integer toUserId);

    /**
     * 查询好友列表（包含关注人和粉丝）
     * @param pageNum
     * @param pageSize
     * @param nickname
     * @return
     */
    public PageInfo<UserFriendVO> searchFriends(Integer pageNum, Integer pageSize, String nickname);

    /**
     * 查询用户列表
     * @param pageNum
     * @param pageSize
     * @param keyWord
     * @return
     */
    public PageInfo<UserFriendVO> searchUsers(Integer pageNum, Integer pageSize, String keyWord);

    /**
     * 获取所有关注人信息
     * @param id 用户id
     * @return
     */
    public List<UserFriend> getAllAttentionsByUserId(Integer id);
}
