package com.fulu.game.core.entity.to;

import com.fulu.game.core.entity.UserTechAuth;
import lombok.Data;

@Data
public class UserTechAuthTO extends UserTechAuth {

    /**
     * 段位ID
     */
    private Integer danId;

    /**
     * 属性ID
     */
    private Integer[] attrId;

    /**
     * 技能认证标签
     */
    private Integer[] tagIds;


}
