package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 订单处理文件表
 * 
 * @author yanbiao
 * @date 2018-04-26 17:51:54
 */
@Data
public class OrderDealFile implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//
	private Integer orderDealId;
	//文件URL
	private String fileUrl;
	//
	private Date createTime;

}
