package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 用户身份认证信息表
 *
 * @author jaycee Deng
 * @date 2018-09-06 14:29:30
 */
@Data
public class UserBodyAuth implements Serializable {
	private static final long serialVersionUID = 1L;

	//主键id
	private Integer id;
	//用户id
	private Integer userId;
	//用户认证的名字
	private String userName;
	//用户身份证号
	private String cardNo;
	//用户身份证正面照片
	private String cardUrl;
	//用户手持身份证照片
	private String cardHandUrl;
	//认证状态  0 未认证  1已通过  2未通过
	private Integer authStatus;
	//管理员id
	private Integer adminId;
	//管理员名称
	private String adminName;
	//备注
	private String remarks;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;
}
