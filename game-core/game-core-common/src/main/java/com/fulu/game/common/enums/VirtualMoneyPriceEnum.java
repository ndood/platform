package com.fulu.game.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 钻石价格枚举类
 *
 * @author Gong ZeChun
 * @date 2018/9/14 10:12
 */
@Getter
@AllArgsConstructor
public enum VirtualMoneyPriceEnum {
    VIRTUAL_MONEY_42(42, "6yuan"),
    VIRTUAL_MONEY_210(210, "30yuan"),
    VIRTUAL_MONEY_686(686, "98yuan"),
    VIRTUAL_MONEY_2086(2086, "298yuan"),
    VIRTUAL_MONEY_4116(4116, "588yuan"),
    VIRTUAL_MONEY_6286(6286, "898yuan");

    private Integer number;
    private String priceStr;
}
