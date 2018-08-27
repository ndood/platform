package com.fulu.game.core.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fulu.game.core.entity.UserFriend;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/8/27 11:26.
 * @Description: 用户好友VO
 */
@Data
public class UserFriendVO extends UserFriend {

    /** 发起操作用户id */
    @NotNull(message = "操作用户不能为空")
    private Integer fromUserId;
    /** 目标用户id */
    @NotNull(message = "目标用户不能为空")
    private Integer toUserId;
    /** 关联查询用户信息时，查询fromUser信息还是toUser信息（1：查toUser信息-如关注人和黑名单；2：fromUser信息-如粉丝） */
    @JsonIgnore
    private Integer type;
    /** 用户头像url */
    private String headPortraitsUrl;
    /** 用户昵称 */
    private String nickname;
    /** 用户性别 */
    private Integer gender;
    /** 搜索用户时，获取到的用户id */
    private Integer userId;
}
