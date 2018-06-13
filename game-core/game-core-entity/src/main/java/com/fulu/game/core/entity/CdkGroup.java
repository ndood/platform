package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * cdk批次表
 * 
 * @author yanbiao
 * @date 2018-06-13 15:34:02
 */
@Data
public class CdkGroup implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键id
	private Integer id;
	//类型(卢克，安图恩，H阿古斯)
	private String type;
	//启用状态(1为启用，0为关闭)
	private Integer status;
	//游戏id
	private Integer categoryId;
	//单价
	private BigDecimal price;
	//数量
	private Integer amount;
	//操作人id
	private Integer adminId;
	//操作人用户名
	private String adminName;
	//渠道商id
	private Integer channelId;
	//渠道商名
	private String channelName;
	//生成时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	//修改时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

}
