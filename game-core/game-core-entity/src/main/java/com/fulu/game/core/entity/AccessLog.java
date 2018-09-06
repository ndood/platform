package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 访问日志记录表
 * 
 * @author shijiaoyun
 * @date 2018-08-30 11:24:56
 */
@Data
public class AccessLog implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//id自增
	private Integer id;
	//访问者id
	private Integer fromUserId;
	//被访问者id
	private Integer toUserId;
	//访问菜单名称逗号间隔
	private String menusName;
	//访问次数
	private Integer count;
	//城市编码
	private String cityCode;
	//城市名称
	private String cityName;
	//创建时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	//修改时间（同一个访问者和被访问者只会有一条记录，通过update_time来查，详细列表通过详情表来查）
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;
	//状态（1：有效；0：无效）
	private Integer status;

}
