package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 个人标签关联表
 * 
 * @author wangbin
 * @date 2018-04-20 15:50:34
 */
@Data
public class PersonTag implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//标签Id
	private Integer tagId;
	//用户ID
	private Integer userId;
	//标签名
	private String name;
	//
	private Date createTime;
	//
	private Date updateTime;

}
