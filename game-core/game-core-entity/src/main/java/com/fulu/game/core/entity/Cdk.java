package com.fulu.game.core.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
    @Excel(name = "序列号", orderNum = "0", width = 30)
    private String series;
    //游戏id
    @Excel(name = "游戏ID", orderNum = "1", width = 15)
    private Integer categoryId;
    //单价
    @Excel(name = "单价", orderNum = "2", width = 15)
    private BigDecimal price;
    //类型
    @Excel(name = "类型", orderNum = "3", width = 15)
    private String type;
    //批次id
    @Excel(name = "批次ID", orderNum = "4", width = 15)
    private Integer groupId;
    //渠道商id
    @Excel(name = "渠道商ID", orderNum = "5", width = 15)
    private Integer channelId;
    //使用状态(0未使用，1已使用)
    @Excel(name = "使用状态", orderNum = "6", replace = {"已使用_true", "未使用_false"}, width = 15)
    private Boolean isUse;
    //使用订单号
    @Excel(name = "对应订单号", orderNum = "7", width = 15)
    private String orderNo;
    //使用时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "使用时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "8", width = 35)
    private Date updateTime;
    //生成时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "生成时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "9", width = 35)
    private Date createTime;
    //是否可用
    private Boolean enable;
}
