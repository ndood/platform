package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 用户兴趣表
 * 
 * @author shijiaoyun
 * @date 2018-08-31 17:39:58
 */
@Data
public class UserInterests implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//兴趣名称
	private String name;
	//排序号
	private Integer sort;
	//创建时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;
	//修改时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date updateTime;
	//数据状态(1：有效；0：无效)
	private Integer status;

}
