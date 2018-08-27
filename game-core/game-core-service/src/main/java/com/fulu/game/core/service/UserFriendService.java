package com.fulu.game.core.service;

import com.alibaba.fastjson.JSONObject;
import com.fulu.game.core.entity.UserFriend;
import com.fulu.game.core.entity.vo.UserFriendVO;
import com.github.pagehelper.PageInfo;

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
    public JSONObject save(UserFriendVO userFriendVO);

    /**
     * 获取关注人列表
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    public PageInfo<UserFriendVO> getAttentions(Integer pageNum, Integer pageSize, Integer userId);

    /**
     * 获取粉丝列表
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    public PageInfo<UserFriendVO> getFans(Integer pageNum, Integer pageSize, Integer userId);

    /**
     * 获取黑名单列表
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    public PageInfo<UserFriendVO> getBlacks(Integer pageNum, Integer pageSize, Integer userId);

    /**
     * 是否是黑名单用户
     * @param fromUserId
     * @param toUserId
     * @return
     */
    public JSONObject isBlackUser(Integer fromUserId, Integer toUserId);

    /**
     * 查询好友列表（包含关注人和粉丝）
     * @param pageNum
     * @param pageSize
     * @param userId
     * @param nickname
     * @return
     */
    public PageInfo<UserFriendVO> searchFriends(Integer pageNum, Integer pageSize, Integer userId, String nickname);

    /**
     * 查询用户列表
     * @param pageNum
     * @param pageSize
     * @param userId
     * @param keyWord
     * @return
     */
    public PageInfo<UserFriendVO> searchUsers(Integer pageNum, Integer pageSize, Integer userId, String keyWord);
}
