package com.fulu.game.common.enums;

import com.fulu.game.common.Constant;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 用户积分枚举类
 * @Author: Gong ZeChun
 * @Date: 2018/7/16 16:17
 */
@Getter
@AllArgsConstructor
public enum UserScoreEnum {

    USER_LOGIN(Constant.USER_LOGIN, 1);

    private String description;
    private Integer score;
}
