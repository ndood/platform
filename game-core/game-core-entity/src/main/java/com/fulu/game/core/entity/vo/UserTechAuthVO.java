package com.fulu.game.core.entity.vo;


import com.fulu.game.common.enums.TechAuthStatusEnum;
import com.fulu.game.core.entity.*;
import lombok.Data;

import java.math.BigDecimal;
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

    private String birth;
    // 用户综合评分
    private BigDecimal userScoreAvg;
    // 用户注册来源类型
    private Integer registerType;
    //关注用户数
    private Integer attentions;
    //粉丝数
    private Integer fansCount;
    //虚拟粉丝数
    private Integer virtualFansCount;
    //历史浏览次数
    private Integer historyBrowseCount;
    //历史被访问次数
    private Integer historyAccessedCount;
    //发布动态数
    private Integer dynamicCount;

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
