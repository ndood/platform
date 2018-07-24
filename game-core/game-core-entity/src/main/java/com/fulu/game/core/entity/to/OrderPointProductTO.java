package com.fulu.game.core.entity.to;


import com.fulu.game.core.entity.OrderPointProduct;
import lombok.Data;


/**
 * 上分订单详情
 *
 * @author wangbin
 * @date 2018-07-24 17:55:45
 */
@Data
public class OrderPointProductTO extends OrderPointProduct {

    //优惠券编号
    private String couponNo;
    //联系方式类型
    private Integer contactType;
    //联系方式
    private String contactInfo;

}
