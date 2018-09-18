package com.fulu.game.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 信息认证表
 *
 * @author wangbin
 * @date 2018-04-20 11:12:13
 */
@Data
public class UserInfoAuth implements Serializable {
    private static final long serialVersionUID = 1L;

    //
    private Integer id;
    //用户ID
    private Integer userId;
    //qq号码
    private String qq;
    //微信号
    private String wechat;
    //是否是驳回提交
    private Boolean isRejectSubmit;
    //主图
    private String mainPicUrl;
    //推送时间间隔
    private Float pushTimeInterval;
    //是否允许导出
    private Boolean allowExport;
    //推荐位排序字段
    private Integer sort;
    //自动问好
    private String autoSayHello;
    //
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    //
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    //代聊客服ID
    private Integer imSubstituteId;

    /**
     * 用户兴趣
     */
    private String interests;
    /**
     * 用户职业
     */
    private String profession;
    /**
     * 用户简介
     */
    private String about;

    //是否开启代聊  0关闭  1开启
    private Boolean openSubstituteIm;
}
