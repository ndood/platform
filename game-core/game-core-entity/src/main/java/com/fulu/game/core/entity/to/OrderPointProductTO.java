package com.fulu.game.core.entity.to;


import com.fulu.game.core.entity.OrderPointProduct;
import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * 上分订单详情
 *
 * @author wangbin
 * @date 2018-07-24 17:55:45
 */
@Data
public class OrderPointProductTO extends OrderPointProduct {

    @NotNull(message = "游戏分类不能为空")
    private Integer categoryId;

    @NotNull(message = "上分类型不能为空")
    private Integer pointType;

    @NotNull(message = "大区不能为空")
    private Integer areaId;

    @NotNull(message = "当前不能为空")
    private Integer gradingPriceId;
    //
    private Integer targetGradingPriceId;

    @NotNull(message = "数量不能为空")
    private Integer amount;

    //优惠券编号
    private String couponNo;
    //联系方式类型
    private Integer contactType;
    //联系方式
    private String contactInfo;


}
