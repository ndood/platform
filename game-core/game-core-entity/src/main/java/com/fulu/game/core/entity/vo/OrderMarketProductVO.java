package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.OrderMarketProduct;
import lombok.Data;


/**
 * 集市订单商品表
 *
 * @author wangbin
 * @date 2018-06-13 17:11:31
 */
@Data
public class OrderMarketProductVO  extends OrderMarketProduct {


    private String statusStr;

    private String series;

}
