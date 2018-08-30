package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 虚拟商品订单表
 *
 * @author Gong Zechun
 * @date 2018-08-30 10:04:09
 */
@Data
public class VirtualProductOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //订单号
    private String orderNo;
    //虚拟商品id
    private String virtualProductId;
    //虚拟商品价格（对应钻石数量）
    private Integer price;
    //发起人id
    private Integer fromUserId;
    //接收人id
    private Integer targetUserId;
    //是否支付（0：否（默认）；1：是）
    private Integer isPay;
    //支付时间
    private Date payTime;
    //备注
    private String remark;
    //修改时间
    private Date updateTime;
    //创建时间
    private Date createTime;

}
