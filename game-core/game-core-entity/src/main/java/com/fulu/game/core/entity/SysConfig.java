package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 系统配置表
 * 
 * @author wangbin
 * @date 2018-05-14 15:11:40
 */
@Data
public class SysConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	@JsonIgnore
	private Integer id;

	private Integer type;

	private String version;
	//配置名
	private String name;
	//配置值
	private String value;
	//注释
	@JsonIgnore
	private String note;
	//
	@JsonIgnore
	private Date createTime;
	//
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

}
