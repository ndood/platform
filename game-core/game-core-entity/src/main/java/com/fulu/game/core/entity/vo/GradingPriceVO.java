package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.GradingPrice;
import lombok.Data;

import java.util.List;


/**
 * 段位定级价格表
 *
 * @author wangbin
 * @date 2018-07-24 16:20:06
 */
@Data
public class GradingPriceVO  extends GradingPrice {

    private String categoryName;


    private List<GradingPriceVO> children;
}
