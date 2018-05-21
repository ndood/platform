package com.fulu.game.core.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券表
 *
 * @author wangbin
 * @date 2018-05-15 17:22:33
 */
@Data
public class Coupon implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    @Excel(name = "优惠券编码",orderNum = "0",width = 15)
    private String couponNo;
    //优惠券组ID
    private Integer couponGroupId;
    //面额
    @Excel(name = "优惠券减额",orderNum = "1")
    private BigDecimal deduction;
    //是否是新用户专享
    @Excel(name = "新用户专享",replace = {"是_true","否_false"},orderNum = "2")
    private Boolean isNewUser;
    //绑定了那个用户
    private Integer userId;
    //领取手机号
    @Excel(name = "领取手机号",orderNum = "3",width = 15)
    private String mobile;
    //是否被使用(0:否,1:是)
    @Excel(name = "是否使用",replace = {"是_true","否_false"},orderNum = "4")
    private Boolean isUse;
    //订单号
    @Excel(name = "订单号",orderNum = "5",width = 15)
    private String orderNo;
    //是否是首次领取
    @Excel(name = "首次领取",replace = {"是_true","否_false"},orderNum = "6")
    private Boolean isFirstReceive ;
    //有效期开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startUsefulTime;
    //有效期结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endUsefulTime;
    //领取时间
    @Excel(name = "领取时间",exportFormat = "yyyy-MM-dd HH:mm:ss",orderNum = "7",width = 20)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date receiveTime;
    //使用时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date useTime;
    //领取IP
    @Excel(name = "领取IP",orderNum = "8")
    private String receiveIp;
    //使用IP
    @Excel(name = "使用IP",orderNum = "9")
    private String useIp;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
