package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 动态点赞表
 * 
 * @author shijiaoyun
 * @date 2018-08-30 11:09:38
 */
@Data
public class DynamicLike implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//评论id
	private Long id;
	//动态id
	private Long dynamicId;
	//点赞用户id
	private Long fromUserId;
	//点赞用户头像URL（冗余字段，提高查询效率）
	private String fromUserHeadUrl;
	//点赞用户昵称（冗余字段，提高查询效率）
	private String fromUserNickname;
	//创建时间
	private Date createTime;
	//状态（1：有效；0：取消赞）
	private Integer status;

}
