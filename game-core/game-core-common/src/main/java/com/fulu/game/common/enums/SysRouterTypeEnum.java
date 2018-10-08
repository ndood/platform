package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/9/20 16:59.
 * @Description:
 */
@AllArgsConstructor
@Getter
public enum SysRouterTypeEnum implements TypeEnum<Integer>  {

    MENUS(1, "菜单"),
    // 权限限定到接口级别
    PERMISSION(2, "权限");

    private Integer type;
    private String msg;
}
