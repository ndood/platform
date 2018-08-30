package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 虚拟商品表
 *
 * @author Gong Zechun
 * @date 2018-08-30 15:14:16
 */
@Data
public class VirtualProduct implements Serializable {
    private static final long serialVersionUID = 1L;

    //
    private Integer id;
    //商品名称
    private String name;
    //商品价格
    private Integer price;
    //1 礼物  2 私照图片组 3 IM解锁图片组 4 IM解锁语音
    private Integer type;
    //排序
    private Integer sort;
    //商品图片地址
    private String objectUrl;
    //
    private String remark;
    //数据状态  1  正常 0已删除
    private Integer delFlag;
    //
    private Date updateTime;
    //
    private Date createTime;

}
