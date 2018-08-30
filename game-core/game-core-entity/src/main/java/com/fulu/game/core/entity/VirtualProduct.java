package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 虚拟商品表
 *
 * @author Gong Zechun
 * @date 2018-08-30 10:01:57
 */
@Data
public class VirtualProduct implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //商品名称
    private String name;
    //商品价格（对应钻石数量）
    private Integer price;
    //商品类型(1：虚拟礼物；2：声音；3：套图)
    private Integer type;
    //对应物品链接
    private String objectUrl;
    //礼物id
    private Integer giftId;
    //套图id
    private Integer groupPicId;
    //备注
    private String remark;
    //修改时间
    private Date updateTime;
    //创建时间
    private Date createTime;

}
