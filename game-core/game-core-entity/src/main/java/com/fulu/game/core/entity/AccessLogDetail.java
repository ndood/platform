package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
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
	private Date createTime;
	//修改时间
	private Date updateTime;
	//状态（1：有效；0：无效）
	private Integer status;

}
