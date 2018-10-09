package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 系统管理员表
 * @author yanbiao
 * @date 2018-04-24 10:20:44
 */
@Data
public class Admin implements Serializable {
	private static final long serialVersionUID = 1L;

	//主键id，自增
	private Integer id;
	//别名（如中文名）
	private String name;
	//用户名
	private String username;
	//密码
	@JsonIgnore
	private String password;
	//密码盐
	@JsonIgnore
	private String salt;
	//状态(0失效,1启用)
	private Integer status;

	private String imId;

	private String imPwd;

	//生成时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;
	//修改时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date updateTime;

	/** 角色id */
	private Integer roleId;

	/** 角色名称 */
	private String roleName;

	/** 超管指定名字 */
	private String adminName;
}
