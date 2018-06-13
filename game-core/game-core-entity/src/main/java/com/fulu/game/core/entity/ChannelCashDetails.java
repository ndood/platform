package com.fulu.game.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 渠道商金额流水表
 *
 * @author yanbiao
 * @date 2018-06-13 15:33:45
 */
@Data
public class ChannelCashDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //管理员id
    private Integer adminId;
    //管理员用户名
    private String adminName;
    //渠道id
    private Integer channelId;
    //加款:1,扣款:2,退款:3
    private Integer action;
    //本次金额
    private BigDecimal money;
    //当前余额
    private BigDecimal sum;
    //订单号
    private String orderNo;
    //备注
    private String remark;
    //生成时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
