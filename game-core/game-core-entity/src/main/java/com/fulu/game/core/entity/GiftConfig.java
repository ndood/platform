package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 礼物配置表
 *
 * @author Gong Zechun
 * @date 2018-08-30 11:35:12
 */
@Data
public class GiftConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //名称
    private String name;
    //礼物价格（对应钻石数量）
    private Integer price;
    //排序
    private Integer sort;
    //图片路径
    private String url;
    //礼物类型（1：动态礼物，预留方便以后扩展）
    private Integer type;
    //创建者id
    private Integer operatorId;
    //创建者名称
    private String operatorName;
    //备注
    private String remark;
    //修改时间
    private Date updateTime;
    //创建时间
    private Date createTime;
    //状态（1：有效；0：无效）
    private Integer status;

}
