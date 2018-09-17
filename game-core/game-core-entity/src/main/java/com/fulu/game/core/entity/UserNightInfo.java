package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 午夜场陪玩师信息表
 *
 * @author Gong Zechun
 * @date 2018-09-17 15:32:26
 */
@Data
public class UserNightInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //用户ID
    private Integer userId;
    //游戏ID
    private Integer categoryId;
    //单位种类
    private Integer type;
    //单位
    private String name;
    //推荐位排序字段
    private Integer sort;
    //操作人id
    private Integer adminId;
    //操作人用户名
    private String adminName;
    //更新时间
    private Date updateTime;
    //创建时间
    private Date createTime;
    //删除标记（0：未删除，1：已删除）
    private Boolean delFlag;
}