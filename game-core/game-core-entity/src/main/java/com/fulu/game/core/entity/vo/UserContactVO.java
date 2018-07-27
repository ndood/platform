package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.UserContact;
import lombok.Data;


/**
 * @author wangbin
 * @date 2018-07-24 19:35:43
 */
@Data
public class UserContactVO extends UserContact {
    /**
     * 微信号
     */
    private String wechat;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * qq号
     */
    private String qq;

    /**
     * 默认类型（1：手机号，2：QQ号，3：微信号）
     */
    private Integer defaultType;
}
