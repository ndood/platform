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


    private String categoryName;

    //订单价格单位
    private String priceUnit;

    private String userHeadUrl;

    private String userNickName;
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

}
