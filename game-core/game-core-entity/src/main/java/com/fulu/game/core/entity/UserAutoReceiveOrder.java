package com.fulu.game.core.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @author wangbin
 * @date 2018-07-26 19:42:13
 */
@Data
public class UserAutoReceiveOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    //
    private Integer id;
    //用户ID
    @Excel(name = "陪玩师id", orderNum = "1", width = 15)
    private Integer userId;
    //用户自己自动接单设置
    @Excel(name = "自动接单设置", orderNum = "2", width = 15, replace = {"开_true",
            "关_false"})
    private Boolean userAutoSetting;
    //游戏技能ID
    private Integer techAuthId;
    //游戏ID
    private Integer categoryId;
    //选择大区ID（为空则为全部）
    private Integer areaId;
    //备注
    @Excel(name = "备注", orderNum = "6", width = 15)
    private String remark;


    //接单范围开始
    @Excel(name = "接单范围开始", orderNum = "7", width = 15)
    private Integer startRank;
    //接单范围结束
    @Excel(name = "接单范围结束", orderNum = "8", width = 15)
    private Integer endRank;

    private String rankIds;
    //自动派单数
    private Integer orderNum;
    //自动派单完成数
    @Excel(name = "上分订单完成数", orderNum = "9", width = 15)
    private Integer orderCompleteNum;
    //派单取消数
    @Excel(name = "上分订单取消数", orderNum = "10", width = 15)
    private Integer orderCancelNum;
    //派单协商仲裁数
    @Excel(name = "上分订单仲裁、协商数", orderNum = "11", width = 20)
    private Integer orderDisputeNum;

    //
    private Integer adminId;
    //
    private String adminName;
    //
    private Date createTime;
    //
    private Date updateTime;

    private Boolean delFlag;

}
