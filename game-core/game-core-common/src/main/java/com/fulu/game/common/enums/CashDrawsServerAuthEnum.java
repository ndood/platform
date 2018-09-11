package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * todo：描述文字
 *
 * @author Gong ZeChun
 * @date 2018/9/10 17:32
 */
@Getter
@AllArgsConstructor
public enum CashDrawsServerAuthEnum implements TypeEnum<Integer> {
    UN_PROCESS(0, "运营未处理"),
    PROCESSED(1, "运营已处理");

    private Integer type;
    private String msg;
}
