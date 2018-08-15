package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 第三方用户信息
 * 
 * @author wangbin
 * @date 2018-08-15 11:56:59
 */
@Data
public class ThirdpartyUser implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//
	private Integer userId;
	//
	private String fqlOpenid;
	//
	private String fqlMobile;
	//
	private Date createTime;
	//
	private Date updateTime;

}
