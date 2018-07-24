package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 
 * 
 * @author wangbin
 * @date 2018-07-24 19:35:43
 */
@Data
public class UserContact implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//用户ID
	private Integer userId;
	//联系方式类型
	private Integer type;
	//联系方式
	private String contact;
	//是否默认
	private Integer isDefault;
	//创建时间
	private Date createTime;
	//
	private Date updateTime;

}
