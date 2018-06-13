package com.fulu.game.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * cdk记录表
 *
 * @author yanbiao
 * @date 2018-06-13 15:33:53
 */
@Data
public class Cdk implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //序列号
    private String series;
    //单价
    private BigDecimal price;
    //类型
    private String type;
    //批次id
    private Integer groupId;
    //使用状态(0未使用，1已使用)
    private Integer isUse;
    //使用订单号
    private String orderNo;
    //使用时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    //生成时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
