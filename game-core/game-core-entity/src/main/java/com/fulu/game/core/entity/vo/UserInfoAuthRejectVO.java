package com.fulu.game.core.entity.vo;


import com.fulu.game.common.enums.UserInfoAuthStatusEnum;
import com.fulu.game.core.entity.UserInfoAuthReject;



/**
 * 用户认证信息驳回表
 *
 * @author wangbin
 * @date 2018-05-28 11:29:00
 */
public class UserInfoAuthRejectVO  extends UserInfoAuthReject {


    private String statusStr;

    public String getStatusStr() {
        if(UserInfoAuthStatusEnum.NOT_PERFECT.getType().equals(getUserInfoAuthStatus())){
            return "驳回";
        }else if(UserInfoAuthStatusEnum.FREEZE.getType().equals(getUserInfoAuthStatus())){
            return "冻结";
        }
        return statusStr;
    }
    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }
}
