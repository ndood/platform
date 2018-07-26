package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.PriceFactor;
import lombok.Data;


/**
 * 价格系数表
 *
 * @author wangbin
 * @date 2018-06-25 18:07:42
 */
@Data
public class PriceFactorVO extends PriceFactor {

    /**
     * 分类名称
     */
    String name;
}
