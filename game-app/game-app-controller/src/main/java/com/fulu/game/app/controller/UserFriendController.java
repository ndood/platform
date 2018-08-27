package com.fulu.game.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.UserFriendVO;
import com.fulu.game.core.service.UserFriendService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/8/27 10:08.
 * @Description: 用户好友
 */

@RestController
@Slf4j
@RequestMapping("/api/v1/userFriend")
public class UserFriendController extends BaseController  {

    @Autowired
    private UserFriendService userFriendService;

    /**
     * 保存关注接口
     * @param userFriendVO
     * @return
     */
    @RequestMapping(value = "save-attention")
    public Result saveAttention(UserFriendVO userFriendVO) {
        // 保存关注信息时不影响黑名单信息
        userFriendVO.setIsBlack(null);
        JSONObject jsonObject = userFriendService.save(userFriendVO);
        return Result.success().data(jsonObject).msg("保存成功！");
    }

    /**
     * 获取关注人列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "get-attentions")
    public Result getAttentions(@RequestParam(required = true) Integer pageNum,
                                @RequestParam(required = true) Integer pageSize,
                                @RequestParam(required = true) Integer userId) {
        PageInfo<UserFriendVO> page = userFriendService.getAttentions( pageNum, pageSize, userId);
        return Result.success().data(page);
    }

    /**
     * 获取粉丝列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "get-fans")
    public Result getFans(@RequestParam(required = true) Integer pageNum,
                                @RequestParam(required = true) Integer pageSize,
                                @RequestParam(required = true) Integer userId) {
        PageInfo<UserFriendVO> page = userFriendService.getFans( pageNum, pageSize, userId);
        return Result.success().data(page);
    }

    /**
     * 保存黑名单接口
     * @param userFriendVO
     * @return
     */
    @RequestMapping(value = "save-black")
    public Result saveBlack(UserFriendVO userFriendVO) {
        // 保存黑名单信息时不影响关注信息
        userFriendVO.setIsAttention(null);
        JSONObject jsonObject = userFriendService.save(userFriendVO);
        return Result.success().data(jsonObject).msg("保存成功！");
    }

    /**
     * 获取黑名单列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "get-blacks")
    public Result getBlacks(@RequestParam(required = true) Integer pageNum,
                                @RequestParam(required = true) Integer pageSize,
                                @RequestParam(required = true) Integer userId) {
        PageInfo<UserFriendVO> page = userFriendService.getBlacks( pageNum, pageSize, userId);
        return Result.success().data(page);
    }

    /**
     * 是否是黑名单用户
     * @param fromUserId
     * @param toUserId
     * @return
     */
    @RequestMapping(value = "is-black-user")
    public Result isBlackUser(@RequestParam(required = true) Integer fromUserId,
                            @RequestParam(required = true) Integer toUserId) {
        JSONObject jsonObject = userFriendService.isBlackUser( fromUserId, toUserId);
        return Result.success().data(jsonObject);
    }


    /**
     * 查询好友列表（包含关注人和粉丝）
     * @param pageNum
     * @param pageSize
     * @param userId 当前用户id
     * @param keyWord 待查询用户昵称
     * @return
     */
    @RequestMapping(value = "search-friends")
    public Result searchFriends(@RequestParam(required = true) Integer pageNum,
                                @RequestParam(required = true) Integer pageSize,
                                @RequestParam(required = true) Integer userId,
                                @RequestParam(required = true) String keyWord) {
        PageInfo<UserFriendVO> page = userFriendService.searchFriends( pageNum, pageSize, userId, keyWord);
        return Result.success().data(page);
    }

    /**
     * 查询用户列表
     * @param pageNum
     * @param pageSize
     * @param userId 当前用户id
     * @param keyWord 待查询用户id或者昵称
     * @return
     */
    @RequestMapping(value = "search-users")
    public Result searchUsers(@RequestParam(required = true) Integer pageNum,
                                @RequestParam(required = true) Integer pageSize,
                                @RequestParam(required = true) Integer userId,
                                @RequestParam(required = true) String keyWord) {
        PageInfo<UserFriendVO> page = userFriendService.searchUsers( pageNum, pageSize, userId, keyWord);
        return Result.success().data(page);
    }

}
