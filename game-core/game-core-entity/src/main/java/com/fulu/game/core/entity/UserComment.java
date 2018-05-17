package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 用户(打手)星级评论表
 * @author yanbiao
 * @date 2018-04-29 13:26:30
 */
@Data
public class UserComment implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键id
	private Integer id;
	//订单关联id
	private String orderNo;
	//玩家id
	private Integer userId;
	//打手id
	private Integer serverUserId;
	//评分(几颗星1-5分)
	private Integer score;
	//平均得星数(不超过5.0,1位小数)
	private BigDecimal scoreAvg;
	//评价具体内容
	private String content;
	//是否匿名评价，默认为否
	private Boolean recordUser;
	//记录生成时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;
	//修改时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date updateTime;

}
