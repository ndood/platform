package com.fulu.game.h5.controller.thunder.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * 迅雷约玩-福利兑换码
 *
 * @author Gong ZeChun
 * @date 2018/10/11 16:37
 */
@Data
public class RedeemCode {
    @Excel(name = "搜狐视频会员兑换码", orderNum = "0")
    private String sohu;

    @Excel(name = "喜马拉雅会员兑换码", orderNum = "1")
    private String ximalaya;

    @Excel(name = "steam游戏CDK", orderNum = "2")
    private String steamSdk;

    @Excel(name = "steam账号", orderNum = "3")
    private String steamAccount;

    @Excel(name = "steam密码", orderNum = "4")
    private String steamPassword;

    @Excel(name = "CIBN会员", orderNum = "5")
    private String cibn;

    public RedeemCode(String sohu, String ximalaya, String steamSdk, String steamAccount, String steamPassword, String cibn) {
        this.sohu = sohu;
        this.ximalaya = ximalaya;
        this.steamSdk = steamSdk;
        this.steamAccount = steamAccount;
        this.steamPassword = steamPassword;
        this.cibn = cibn;
    }

    public RedeemCode() {
    }
}