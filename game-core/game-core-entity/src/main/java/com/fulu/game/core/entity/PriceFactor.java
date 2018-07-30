package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 价格系数表
 *
 * @author wangbin
 * @date 2018-06-26 14:30:55
 */
@Data
public class PriceFactor implements Serializable {
    private static final long serialVersionUID = 1L;

    //
    private Integer id;
    //价格系数
    private BigDecimal factor;
    //来源id（对应t_regist_source表的主键id）
    private Integer sourceId;
    //
    private String categoryIds;
    //
    private Integer adminId;
    //
    private String adminName;
    //
    private Date createTime;

}
