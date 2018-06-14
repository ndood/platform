package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.Order;
import lombok.Data;

@Data
public class MarketOrderVO extends Order{

    private String categoryIcon;

    private String categoryName;

    private String statusStr;

}
