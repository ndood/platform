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
}
