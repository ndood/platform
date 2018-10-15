package com.fulu.game.core.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 聊天室redis缓存的vo
 */
@Data
public class UserChatRoomVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //用户ID
    private Integer userId;

    //用户头像
    private String headPortraitsUrl;

    //昵称
    private String nickname;

    //性别
    private Integer gender;

    //年龄
    private Integer age;

    //房间号
    private String roomNo;

    //房间身份
    private Integer roomRole;

    //房间名称
    private String roomName;

    //房间图标
    private String roomIcon;

    //是否收藏
    private Boolean isCollect;

    //麦位置
    private Integer micIndex;

    //送出礼物数量
    private BigDecimal giftPrice;

    //是否是黑名单用户
    private Boolean blackList;

    //用户认证游戏图标列表
    private List<String>  techCategoryIcons;

    //用户认证技能分类列表
    private List<Integer> techCategoryIds;


}
