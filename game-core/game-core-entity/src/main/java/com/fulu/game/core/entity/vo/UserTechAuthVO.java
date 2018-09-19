package com.fulu.game.core.entity.vo;


import com.fulu.game.common.enums.TechAuthStatusEnum;
import com.fulu.game.core.entity.*;
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


    //原来标签列表(待废弃)
    private List<TechTag> tagList;
    //段位信息(待废弃)
    private UserTechInfo danInfo;
    //不通过原因
    private String reason;
    //游戏信息
    private Category category;

    //认证状态文字
    private String statusStr;

    /**
     * 单位列表
     */
    private List<SalesMode> salesModeList;


    private String nickname;

    private Integer gender;

    private List<TagVO> groupTags;

    private List<TechAttrVO> groupAttrs;

    private Boolean autoOrder;

    public String getStatusStr() {
        return TechAuthStatusEnum.getMsgByType(getStatus());
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

}
