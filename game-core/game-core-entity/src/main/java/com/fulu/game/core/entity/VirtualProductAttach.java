package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 虚拟商品附件表
 *
 * @author Gong Zechun
 * @date 2018-08-30 15:05:30
 */
@Data
public class VirtualProductAttach implements Serializable {
    private static final long serialVersionUID = 1L;

    //
    private Integer id;
    //文件归属人的ID
    private Integer userId;
    //虚拟商品ID
    private Integer virtualProductId;
    //文件对应的URL地址
    private String url;
    //
    private Date createTime;
    //
    private Date updateTime;

}
