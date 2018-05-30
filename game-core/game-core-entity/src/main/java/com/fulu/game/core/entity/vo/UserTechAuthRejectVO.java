package com.fulu.game.core.entity.vo;


import com.fulu.game.common.enums.TechAuthStatusEnum;
import com.fulu.game.common.enums.UserInfoAuthStatusEnum;
import com.fulu.game.core.entity.UserTechAuthReject;
import lombok.Data;


/**
 * 技能认证信息驳回表
 *
 * @author wangbin
 * @date 2018-05-28 18:07:18
 */
@Data
public class UserTechAuthRejectVO  extends UserTechAuthReject {

    private String statusStr;

    public String getStatusStr() {
        if(TechAuthStatusEnum.NO_AUTHENTICATION.getType().equals(getUserTechAuthStatus())){
            return "驳回";
        }else if(TechAuthStatusEnum.FREEZE.getType().equals(getUserTechAuthStatus())){
            return "冻结";
        }
        return statusStr;
    }
    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }
}
