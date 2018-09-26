package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 派单设置
 * 
 * @author wangbin
 * @date 2018-09-20 18:43:50
 */
@Data
public class AssignOrderSetting implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//用户ID
	private Integer userId;
	//激活
	private Boolean enable;
	//设置开始时间
	private String beginTime;
	//设置结束时间
	private String endTime;
	//每周那天接单
	private Integer weekDayBins;
	//
	private Date createTime;
	//
	private Date updateTime;

}
