package com.fulu.game.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 渠道商表
 *
 * @author yanbiao
 * @date 2018-06-13 15:33:34
 */
@Data
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //渠道商名
    private String name;
    //渠道商唯一标识
    private String appid;
    //渠道商访问的token
    private String appkey;
    //余额(初始为0.00)
    private BigDecimal balance;
    //添加人id
    private Integer adminId;
    //添加人用户名
    private String adminName;
    //生成时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    //修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
