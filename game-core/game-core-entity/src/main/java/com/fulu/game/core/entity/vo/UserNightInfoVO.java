package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.UserNightInfo;
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
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 游戏种类名称
     */
    private String categoryName;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 所有可选的技能选项
     */
    List<UserTechAuthVO> allUserTechs;
}
