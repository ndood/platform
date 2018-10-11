package com.fulu.game.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * banner管理表
 *
 * @author yanbiao
 * @date 2018-05-25 12:15:22
 */
@Data
public class Banner implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //是否启用
    private Boolean disable;
    //排序id
    private Integer sort;
    //操作人id
    private Integer operatorId;
    //操作人用户名
    private String operatorName;
    // 平台类型（1：小程序；2：App）
    private Integer platformType;
    //图片url
    private String picUrl;
    //跳转类型
    private Integer redirectType;
    //跳转地址(0表示无1表示小程序内2表示url)
    private String redirectUrl;
    //生成时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    //修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    //开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    //结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

}
