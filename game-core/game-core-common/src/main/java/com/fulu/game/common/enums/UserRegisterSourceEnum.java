package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户注册来源
 *
 * @author shijiaoyun
 * @date 2018/9/14 16:28
 */
@Getter
@AllArgsConstructor
public enum UserRegisterSourceEnum implements TypeEnum<Integer> {

    PLAY(1, "小程序"),
    APP(2, "APP");
    private Integer type;
    private String msg;
}
