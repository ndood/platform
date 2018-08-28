package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 
 * 
 * @author jaycee Deng
 * @date 2018-08-27 17:54:37
 */
@Data
public class AdminImLog implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//
	private String fromImId;
	//
	private String targetImId;
	//
	private String content;
	//
	private Date sendtime;

}
