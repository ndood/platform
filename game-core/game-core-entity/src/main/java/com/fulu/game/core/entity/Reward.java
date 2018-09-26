package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
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
	private Integer id;
	//来源id
	private Integer resourceId;
	//来源类型（1：动态打赏；）
	private Integer resourceType;
	//礼物id
	private Integer giftId;
	//礼物图标（冗余字段，提高查询效率）
	private String giftUrl;
	//礼物钻石数
	private Integer giftPrice;
	//给打赏用户id
	private Integer fromUserId;
	//给打赏用户头像URL（冗余字段，提高查询效率）
	private String fromUserHeadUrl;
	//给打赏用户昵称（冗余字段，提高查询效率）
	private String fromUserNickname;
	//给打赏用户性别(默认0：不公开；1：男；2：女)
	private Integer fromUserGender;
	//获得打赏用户id(预留)
	private Integer toUserId;
	//获得打赏用户头像URL（冗余字段，提高查询效率）
	private String toUserHeadUrl;
	//获得打赏用户昵称（冗余字段，提高查询效率）
	private String toUserNickname;
	//获得打赏用户性别(默认0：不公开；1：男；2：女)
	private Integer toUserGender;
	//创建时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	//状态（1：有效；0：无效）
	private Integer status;
	//打赏订单号
	private String orderNo;
	//评论用户年龄
	private Integer age;


}
