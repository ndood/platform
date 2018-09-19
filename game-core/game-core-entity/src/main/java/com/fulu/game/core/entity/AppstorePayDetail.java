package com.fulu.game.core.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;


/**
 * 
 * 
 * @author wangbin
 * @date 2018-09-19 20:45:00
 */
@Data
public class AppstorePayDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	public AppstorePayDetail() {
	}

	public AppstorePayDetail(String transactionId, String originalTransactionId, String productId, Integer quantity, String purchaseDate) {
		this.transactionId = transactionId;
		this.originalTransactionId = originalTransactionId;
		this.productId = productId;
		this.quantity = quantity;
		this.purchaseDate = purchaseDate;

	}

	//
	private Integer id;
	//
	private String transactionId;
	//
	private String originalTransactionId;
	//
	private String productId;
	//
	private Integer quantity;
	//
	private String purchaseDate;
	//
	private String orderNo;
	//
	private Date createDate;
	//
	private Date updateDate;

}
