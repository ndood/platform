package com.fulu.game.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 技能认证表
 *
 * @author wangbin
 * @date 2018-04-23 11:17:40
 */
@Data
public class UserTechAuth implements Serializable {
    private static final long serialVersionUID = 1L;

    //
    private Integer id;
    //游戏ID
    private Integer categoryId;
    //用户ID
    private Integer userId;
    //技能评分图片
    private String gradePicUrl;
    //技能描述
    private String description;
    //状态(0未认证,1认证中，2正常，3冻结)
    private Integer status;
    //是否激活
    private Boolean isActivate;

    private Boolean isMain;

    // 用户该技能接单数
    private Integer orderCount;

    //用户最大定价价格
    private BigDecimal maxPrice;
    //用户技能等级
    private String level;

    //虚拟接单数
    private Integer virtualOrderCount;

    //语音地址
    private String voice;
    //语音时长
    private Integer voiceDuration;

    //技能认证来源
    private Integer resourceType;

    // 技能综合评分
    private BigDecimal scoreAvg;

    private String mobile;

    private String categoryName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;


    /** 技能等级id */
    private Integer techLevelId;

    /** 技能等级名称 */
    private String techLevelName;
}
