package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 陪玩师评价用户表
 * 
 * @author shijiaoyun
 * @date 2018-09-20 11:26:51
 */
@Data
public class ServerComment implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//订单编号
	private String orderNo;
	//下单用户id（被评论用户id）
	private Integer userId;
	//陪玩师id（评论用户id）
	private Integer serverUserId;
	//评分(几颗星1-5分)
	private Integer score;
	//平均得星数(不超过5.0,1位小数)
	private BigDecimal scoreAvg;
	//评论内容（不超过100个字）
	private String content;
	//评论创建时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	//修改时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

}
