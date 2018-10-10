package com.fulu.game.play.controller;

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
@RequestMapping("/api/v1/user-friend")
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
        userFriendService.save(userFriendVO);
        return Result.success().data(userFriendVO).msg("保存成功！");
    }

    /**
     * 获取关注人列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "get-attentions")
    public Result getAttentions(@RequestParam(required = true) Integer pageNum,
                                @RequestParam(required = true) Integer pageSize) {
        PageInfo<UserFriendVO> page = userFriendService.getAttentions( pageNum, pageSize);
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
                                @RequestParam(required = true) Integer pageSize) {
        PageInfo<UserFriendVO> page = userFriendService.getFans( pageNum, pageSize);
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
        userFriendService.save(userFriendVO);
        return Result.success().data(userFriendVO).msg("保存成功！");
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
                            @RequestParam(required = false) String nickname) {
        PageInfo<UserFriendVO> page = userFriendService.getBlacks( pageNum, pageSize, nickname);
        return Result.success().data(page);
    }

    /**
     * 是否是黑名单用户
     * @param toUserId
     * @return
     */
    @RequestMapping(value = "is-black-user")
    public Result isBlackUser(@RequestParam(required = true) Integer toUserId) {
        boolean isBlack = userFriendService.isBlackUser( toUserId);
        return Result.success().data(isBlack);
    }


    /**
     * 查询好友列表（包含关注人和粉丝）
     * @param pageNum
     * @param pageSize
     * @param keyWord 待查询用户昵称
     * @return
     */
    @RequestMapping(value = "search-friends")
    public Result searchFriends(@RequestParam(required = true) Integer pageNum,
                                @RequestParam(required = true) Integer pageSize,
                                @RequestParam(required = true) String keyWord) {
        PageInfo<UserFriendVO> page = userFriendService.searchFriends( pageNum, pageSize, keyWord);
        return Result.success().data(page);
    }

    /**
     * 查询用户列表
     * @param pageNum
     * @param pageSize
     * @param keyWord 当前用户id
     * @param keyWord 待查询用户id或者昵称
     * @return
     */
    @RequestMapping(value = "search-users")
    public Result searchUsers(@RequestParam(required = true) Integer pageNum,
                                @RequestParam(required = true) Integer pageSize,
                                @RequestParam(required = true) String keyWord) {
        PageInfo<UserFriendVO> page = userFriendService.searchUsers( pageNum, pageSize, keyWord);
        return Result.success().data(page);
    }

}
