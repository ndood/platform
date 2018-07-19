package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


/**
 * 订单处理表(申诉或验收记录)
 * 
 * @author wangbin
 * @date 2018-07-18 15:40:34
 */
@Data
public class OrderDeal implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//订单号
	private String orderNo;
	//订单申诉和仲裁对应ID
	private Integer orderEventId;
	//订单协商标题
	private String title;
	//
	private Integer userId;
	//订单处理类型
	private Integer type;
	//处理过程备注
	private String remark;
	//创建时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

}
