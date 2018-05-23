package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.Coupon;
import lombok.Data;

/**
 * 优惠券表
 *
 * @author wangbin
 * @date 2018-05-15 10:41:12
 */
@Data
public class CouponVO  extends Coupon {

    //是否是老用户
    private Boolean isOldUser;
    //是否过期
    private Boolean overdue;

}
