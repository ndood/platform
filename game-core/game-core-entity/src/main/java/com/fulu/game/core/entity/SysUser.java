package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;



/**
 * @author wangbin
 * @email ${email}
 * @date 2018-04-14 22:35:46
 */
public class SysUser implements Serializable {

	private static final long serialVersionUID = 1L;
	//
	private Integer id;
	//
	private String name;
	//
	private String username;
	//
	private String password;
	//
	private String status;
	//
	private Date createTime;
	//
	private Date updateTime;


	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}
}
