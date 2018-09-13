package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryAuthStatusEnum implements TypeEnum<Integer>{

    //未认证即需审核
    UNAUTH(0, "需审核"),
    // 审核中
    AUTHING(1, "审核中"),
    //被拒绝
    REFUSED(2, "被拒绝"),
    //暂用不到
    PASS(3,"审核通过");

    private Integer type;
    private String msg;

}
