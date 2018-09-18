package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.SalesMode;
import com.fulu.game.core.entity.UserNightInfo;
import com.fulu.game.core.entity.UserTechAuth;
import lombok.Data;

import java.util.List;


/**
 * 午夜场陪玩师信息表
 *
 * @author Gong Zechun
 * @date 2018-09-17 15:32:26
 */
@Data
public class UserNightInfoVO extends UserNightInfo {
    /**
     * 性别
     */
    private Integer gender;

    /**
     * 所有可选的技能选项
     */
    List<UserTechAuth> allUserTechs;

    /**
     * 所有可选的单位选项
     */
    List<SalesMode> allSalesModes;
}
