package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.Order;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailsVO extends Order {

    private String serverHeadUrl;

    private String serverNickName;

    //陪玩师年龄
    private Integer serverAge;
    //陪玩师平均分
    private BigDecimal serverScoreAvg;
    //陪玩师性别
    private Integer serverGender;
    //陪玩师收入
    private BigDecimal serverIncome;


    private String categoryName;

    //订单价格单位
    private String priceUnit;

    private String userHeadUrl;

    private String userNickName;

    private Integer userAge;

    private Integer userGender;

    private BigDecimal userScoreAvg;

    //陪玩师是否已经评价过用户
    private Boolean  isCommentedUser;


    //订单状态
    private String statusStr;
    //订单状态描述
    private String statusNote;
    private String categoryIcon;
    //倒计时
    private Long countDown;
    private Integer productId;

    private Integer identity;

    private Integer commentScore;

    private String commentContent;
    
    private Boolean waitingRead;

    private Integer productAmount;
    
    private String productUnit;
}
