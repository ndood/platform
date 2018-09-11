package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 动态Push消息推送表
 * 
 * @author shijiaoyun
 * @date 2018-09-11 10:24:20
 */
@Data
public class DynamicPushMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//被关注用户id
	private Integer dynamicId;
	//push消息发送用户id
	private Integer fromUserId;
	//push消息发送用户昵称
	private String fromUserNickname;
	//push消息发送用户头像url
	private String fromUserHeadUrl;
	//push消息接收用户id
	private Integer toUserId;
	//push消息类型（1：点赞；2：评论；3打赏）
	private Integer pushType;
	//push消息内容
	private String pushContent;
	//push消息扩展内容
	private String pushExtras;
	//创建时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	//修改时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;
	//状态（0：有效；1：无效）
	private Integer isDel;

}
