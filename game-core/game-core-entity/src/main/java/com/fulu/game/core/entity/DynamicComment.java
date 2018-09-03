package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 动态评论表
 * 
 * @author shijiaoyun
 * @date 2018-08-30 11:21:42
 */
@Data
public class DynamicComment implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//id
	private Long id;
	//动态id(用户查询评论的所有回复信息)
	private Long dynamicId;
	//目标id（当reply_type=1时为评论id，回复的父id）
	private Long commentId;
	//类型（1：评论动态；2：评论动态的评论）
	private Integer commentType;
	//评论的内容
	private String content;
	//用户id
	private Long fromUserId;
	//用户头像URL（冗余字段，提高查询效率）
	private String fromUserHeadUrl;
	//用户昵称（冗余字段，提高查询效率）
	private String fromUserNickname;
	//用户性别(默认0：不公开；1：男；2：女)
	private Integer fromUserGender;
	//目标用户id
	private Long toUserId;
	//目标用户头像URL（冗余字段，提高查询效率）
	private String toUserHeadUrl;
	//目标用户昵称（冗余字段，提高查询效率）
	private String toUserNickname;
	//目标用户性别(默认0：不公开；1：男；2：女)
	private Integer toUserGender;
	//创建时间
	private Date createTime;
	//状态（1：有效；0：无效）
	private Integer status;

}
