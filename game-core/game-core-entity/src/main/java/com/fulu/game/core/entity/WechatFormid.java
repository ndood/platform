package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 
 * 
 * @author wangbin
 * @date 2018-05-11 10:35:21
 */
@Data
public class WechatFormid implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//
	private Integer userId;
	//
	private String formId;

	private Integer type;

	private String openId;
	//
	private Date createTime;

}
