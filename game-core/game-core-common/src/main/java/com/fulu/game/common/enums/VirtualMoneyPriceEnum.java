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
    VIRTUAL_MONEY_42(42L, "6yuan"),
    VIRTUAL_MONEY_210(210L, "30yuan"),
    VIRTUAL_MONEY_686(686L, "98yuan"),
    VIRTUAL_MONEY_2086(2086L, "298yuan"),
    VIRTUAL_MONEY_4116(4116L, "588yuan"),
    VIRTUAL_MONEY_6286(6286L, "898yuan");

    private Long number;
    private String priceStr;


    public static long getNumberByPriceStr(String priceStr){
        for(VirtualMoneyPriceEnum priceEnum : VirtualMoneyPriceEnum.values()){
            if(priceEnum.getPriceStr().equals(priceStr)){
                return priceEnum.getNumber();
            }
        }
        return 0;
    }

}
