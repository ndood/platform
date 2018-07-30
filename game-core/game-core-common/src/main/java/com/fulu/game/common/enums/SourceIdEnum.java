package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SourceId(注册来源id)枚举类
 *
 * @author Gong ZeChun
 * @date 2018/7/30 11:41
 */
@Getter
@AllArgsConstructor
public enum SourceIdEnum implements TypeEnum<Integer> {

    PILOT(1, "领航"),
    CHINA_JOY(31, "ChinaJoy");

    private Integer type;
    private String msg;
}
