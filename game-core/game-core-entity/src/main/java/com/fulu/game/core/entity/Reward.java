package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 打赏记录表
 * 
 * @author shijiaoyun
 * @date 2018-08-30 11:14:29
 */
@Data
public class Reward implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Long id;
	//来源id
	private Long resourceId;
	//来源类型（1：动态打赏；）
	private Integer resourceType;
	//礼物id
	private Long giftId;
	//礼物图标（冗余字段，提高查询效率）
	private String giftUrl;
	//给打赏用户id
	private Long fromUserId;
	//给打赏用户头像URL（冗余字段，提高查询效率）
	private String fromUserHeadUrl;
	//给打赏用户昵称（冗余字段，提高查询效率）
	private String fromUserNickname;
	//给打赏用户性别(默认0：不公开；1：男；2：女)
	private Integer fromUserGender;
	//获得打赏用户id(预留)
	private Long toUserId;
	//获得打赏用户头像URL（冗余字段，提高查询效率）
	private String toUserHeadUrl;
	//获得打赏用户昵称（冗余字段，提高查询效率）
	private String toUserNickname;
	//获得打赏用户性别(默认0：不公开；1：男；2：女)
	private Integer toUserGender;
	//创建时间
	private Date createTime;
	//状态（1：有效；0：无效）
	private Integer status;
	//打赏订单号
	private String orderNo;

}
