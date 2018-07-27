package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.UserWechatGroupShare;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 用户微信群分享表
 *
 * @author Gong Zechun
 * @date 2018-07-25 10:41:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserWechatGroupShareVO extends UserWechatGroupShare {

    /**
     * 来源id
     */
    private Integer sourceId;

    /**
     * 优惠券发放状态： 0：发放失败（默认）；1：发放成功
     */
    private Integer coupouStatus;

    /**
     * 优惠券通道是否可用（1：是； 0：否（在活动中途，优惠券通道可以被人为关闭））
     */
    private Integer isCouponAvailable;
}
