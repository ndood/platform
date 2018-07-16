package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 用户积分枚举类
 * @Author: Gong ZeChun
 * @Date: 2018/7/16 16:17
 */
@Getter
@AllArgsConstructor
public enum ScoreEnum{

    ADD_LOGIN(1, "用户登录加分"),
    ADD_GOOD_COMMENT(2, "用户好评加分"),
    MINUS_BAD_COMMENT(-1, "用户差评扣分");

    private Integer score;
    private String description;
}
