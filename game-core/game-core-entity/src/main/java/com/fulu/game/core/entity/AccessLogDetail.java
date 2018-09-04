package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 访问日志详情表
 * 
 * @author shijiaoyun
 * @date 2018-08-30 11:27:41
 */
@Data
public class AccessLogDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//id自增
	private Long id;
	//访问日志id
	private Long accessLogId;
	//访问过的菜单名称逗号间隔
	private String menusName;
	//城市编码
	private String cityCode;
	//城市名称
	private String cityName;
	//创建时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	//修改时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;
	//状态（1：有效；0：无效）
	private Integer status;

}
