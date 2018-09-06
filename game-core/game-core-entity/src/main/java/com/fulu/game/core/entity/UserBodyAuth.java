package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 用户身份认证信息表
 * 
 * @author jaycee.Deng
 * @date 2018-09-05 17:40:37
 */
@Data
public class UserBodyAuth implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//
	private Integer userId;
	//用户认证的名字
	private String userName;
	//用户身份证号
	private String cardNo;
	//用户身份证正面照片
	private String cardFrontUrl;
	//用户身份证背面照片
	private String cardBackUrl;
	//认证状态  0 未审核  1已通过   1未通过
	private Integer authStatus;
	//
	private String remarks;
	//
	private Date createTime;
	//
	private Date updateTime;

}
