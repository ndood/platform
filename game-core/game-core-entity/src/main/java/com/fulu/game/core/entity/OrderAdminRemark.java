package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 
 * 
 * @author jaydee.Deng
 * @date 2018-09-17 16:42:51
 */
@Data
public class OrderAdminRemark implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//订单号
	private Integer orderId;
	//处理的运营ID
	private Integer agentAdminId;
	//处理的运营名字
	private String agentAdminName;
	//订单备注
	private String remark;
	//数据创建时间
	private Date createTime;
	//数据更新时间
	private Date updateTime;

}
