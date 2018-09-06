package com.fulu.game.core.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 用户身份认证信息表
 *
 * @author jaycee Deng
 * @date 2018-09-06 14:29:30
 */
@Data
public class UserBodyAuth implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    @Excel(name = "申请id", orderNum = "1", width = 15)
    private Integer id;
    //用户id
    @Excel(name = "申请人id", orderNum = "4", width = 15)
    private Integer userId;
    //用户认证的名字
    @Excel(name = "真实姓名", orderNum = "6", width = 15)
    private String userName;
    //用户身份证号
    @Excel(name = "身份证号码", orderNum = "7", width = 15)
    private String cardNo;
    //用户身份证正面照片
    @Excel(name = "身份证照片", orderNum = "8", width = 15)
    private String cardUrl;
    //用户手持身份证照片
    @Excel(name = "手持身份证照片", orderNum = "9", width = 15)
    private String cardHandUrl;
    //认证状态  0 未认证  1已通过  2未通过
    @Excel(name = "运营审批人", orderNum = "11", width = 15, replace = {"未认证_0", "已通过_1", "未通过_2"})
    private Integer authStatus;
    //管理员id
    private Integer adminId;
    //管理员名称
    @Excel(name = "运营审批人", orderNum = "10", width = 15)
    private String adminName;
    //备注
    @Excel(name = "拒绝理由", orderNum = "12", width = 15)
    private String remarks;
    //创建时间
    @Excel(name = "申请时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "2", width = 20)
    private Date createTime;
    //更新时间
    @Excel(name = "操作时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "13", width = 20)
    private Date updateTime;
}
