package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.TechTag;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.UserTechInfo;
import lombok.Data;

import java.util.List;

/**
 * 技能认证表
 *
 * @author wangbin
 * @date 2018-04-23 11:17:40
 */
@Data
public class UserTechAuthVO extends UserTechAuth {

    /**
     * 技能认证标签
     */
    private Integer[] tagIds;

    /**
     * 段位ID
     */
    private Integer danId;

    /**
     * 标签列表
     */
    private List<TechTag> tagList;

    /**
     * 段位信息
     */
    private UserTechInfo danInfo;

    /**
     * 游戏信息
     */
    private Category category;

    private Integer requireCount;

    private String nickname;
    private String startTime;
    private String endTime;

}
