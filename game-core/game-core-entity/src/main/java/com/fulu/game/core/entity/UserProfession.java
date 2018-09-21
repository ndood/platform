package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 用户职业表
 * 
 * @author shijiaoyun
 * @date 2018-09-21 14:29:58
 */
@Data
public class UserProfession implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//职业名称
	private String name;
	//排序号
	private Integer sort;
	//创建时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;
	//修改时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date updateTime;
	//删除标志（1：删除；0：未删除）
	private Integer isDel;

}
