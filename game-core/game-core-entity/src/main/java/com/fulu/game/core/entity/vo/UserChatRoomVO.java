package com.fulu.game.core.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 聊天室redis缓存的vo
 */
@Data
public class UserChatRoomVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //用户ID
    private Integer id;

    //昵称
    private String nickname;

    //性别
    private Integer gender;

    //年龄
    private Integer age;

    //陪玩师的综合评分
    private BigDecimal scoreAvg;




}
