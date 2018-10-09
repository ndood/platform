package com.fulu.game.core.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 零钱流水表
 *
 * @author yanbiao
 * @date 2018-04-25 14:59:54
 */
@Data
public class MoneyDetails implements Serializable {
    private static final long serialVersionUID = 1234567890L;

    //主键id自增
    @Excel(name = "记录id", orderNum = "0", width = 15)
    private Integer id;
    //操作者id(用户或管理员)
    @Excel(name = "操作人", orderNum = "7", width = 15)
    private Integer operatorId;
    //对象id(对谁加款或者提款)
    @Excel(name = "用户id", orderNum = "1", width = 15)
    private Integer targetId;
    //金额(默认0.00)
    @Excel(name = "金额", orderNum = "5", width = 15)
    private BigDecimal money;
    //-1提现，1加零钱，2陪玩订单入账，3拒绝提现返款，4魅力值提现，5、余额兑换钻石，6、余额充值，7、扣除零钱
    @Excel(name = "状态", orderNum = "2", width = 10, replace = {"加零钱_1", "扣零钱_7", "加零钱_null"})
    private Integer action;
    //action为-1时关联t_cash_draws表
    private Integer cashId;
    //action后的金额与用户的金额同步
    private BigDecimal sum;
    //备注
    @Excel(name = "备注", orderNum = "9", width = 40)
    private String remark;
    //记录生成时间
    @Excel(name = "操作时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "8", width = 25)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /** 用户类型（1：普通用户；4：马甲用户） */
    @Excel(name = "身份", orderNum = "6", width = 10, replace = {"用户_1", "马甲_4", "用户_null"})
    private Integer userType;

}
