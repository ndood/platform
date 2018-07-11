package com.fulu.game.core.entity.vo;


import com.fulu.game.common.enums.TechAuthStatusEnum;
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
public class UserTechAuthVO  extends UserTechAuth {


    //原来标签列表(待废弃)
    private List<TechTag> tagList;
    //段位信息(待废弃)
    private UserTechInfo danInfo;
    //不通过原因
    private String reason;
    //游戏信息
    private Category category;
    //还需要好友认证数量(待废弃)
    private Integer requireCount;
    //认证状态文字
    private String statusStr;
    //认证数显示
    private String approveCountStr;

    private String nickname;

    private Integer gender;



    private List<TagVO> groupTags;

    private List<TechAttrVO> groupAttrs;


    public String getStatusStr() {
        return TechAuthStatusEnum.getMsgByType(getStatus());
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

}
