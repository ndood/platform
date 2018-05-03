package com.fulu.game.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


/**
 * 平台流水表
 * 
 * @author wangbin
 * @date 2018-05-02 17:10:26
 */
@Data
public class PlatformMoneyDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//
	private String remark;
	//
	private BigDecimal money;
	//
	private BigDecimal sum;
	//
	private Date createTime;

}
