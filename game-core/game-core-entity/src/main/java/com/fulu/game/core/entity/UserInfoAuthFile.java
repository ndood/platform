package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 信息认证文件表（图片、声音）
 * 
 * @author wangbin
 * @date 2018-04-20 11:12:13
 */
@Data
public class UserInfoAuthFile implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//文件名称
	private String name;
	//关联信息认证ID
	private Integer infoAuthId;
	//类型(1图片,2声音)
	private Integer type;
	//
	private String url;
	//扩展名
	private String ext;
	//
	private Date createTime;
	//
	private Date updateTime;

}
