package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 信息认证文件临时表（图片、声音）
 *
 * @author Gong Zechun
 * @date 2018-07-20 13:34:08
 */
@Data
public class UserInfoAuthFileTemp implements Serializable {
	private static final long serialVersionUID = 1L;

	//
	private Integer id;
	//用户ID
	private Integer userId;
	//关联信息认证ID
	private Integer infoAuthId;
	//文件名称
	private String name;
	//类型(1写真图片,2声音,3主图)
	private Integer type;
	//
	private String url;
	//
	private Integer duration;
	//扩展名
	private String ext;
	//
	private Date createTime;
	//
	private Date updateTime;

}
