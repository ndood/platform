package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


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
	//手机号
	private String mobile;
	//qq号码
	private String qq;
	//微信号
	private String wechat;
	//是否是驳回提交
	private Boolean isRejectSubmit;
	//用户信息是否在平台内展示（0：不展示，1：展示）
	private Integer isPlatformShow;
	//主图
	private String mainPicUrl;
	//推送时间间隔
	private Float pushTimeInterval;
	//是否允许导出
	private Boolean allowExport;
	//
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	//
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;


}
