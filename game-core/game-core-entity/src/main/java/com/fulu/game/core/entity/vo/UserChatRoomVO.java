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

    //接单率
    private BigDecimal orderRate;

    //满意度评分
    private BigDecimal satisfy;

    //房间号
    private String roomNo;

    //房间身份
    private Integer roomRole;

    //房间名称
    private String roomName;
    //房间图标
    private String roomIcon;

    //房间公告
    private String notice;

    //房间标语
    private String slogan;

    //是否收藏
    private Boolean isCollect;

    //虚拟在线人数
    private Integer virtualPeople;

    //房间总人数
    private Long people;

    //麦位置
    private Integer micIndex;

    //送出礼物数量
    private BigDecimal giftPrice;

    //技能图标列表
    private List<String> techIcons;

}
