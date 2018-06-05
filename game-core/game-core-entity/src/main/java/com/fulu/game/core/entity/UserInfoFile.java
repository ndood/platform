package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 用户信息文件表(图片、声音)
 * 
 * @author wangbin
 * @date 2018-04-20 11:12:13
 */
@Data
public class UserInfoFile implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//
	private String name;
	//
	private Integer userId;
	//
	private Integer type;
	//
	private String url;
	//
	private String ext;
	//
	private Date createTime;
	//
	private Date updateTime;

}
