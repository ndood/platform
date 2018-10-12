package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.ThunderCode;
import lombok.Data;


/**
 * 迅雷活动兑换码
 *
 * @author Gong Zechun
 * @date 2018-10-12 09:50:54
 */
@Data
public class ThunderCodeVO extends ThunderCode {

    /**
     * 搜狐兑换码
     */
    private String sohuCode;

    /**
     * 喜马拉雅兑换码
     */
    private String ximaCode;

    /**
     * steamSdk
     */
    private String steamSdk;

    /**
     * steam账号
     */
    private String steamAccount;

    /**
     * steam密码
     */
    private String steamPassword;

    /**
     * cibn兑换码
     */
    private String cibnCode;
}
