package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 用户活动奖励表
 * 
 * @author wangbin
 * @date 2018-09-28 14:32:06
 */
@Data
public class ActivityUserAward implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//用户ID
	private Integer userId;
	//活动ID
	private Integer activityId;
	//创建时间
	private Date createTime;

}
