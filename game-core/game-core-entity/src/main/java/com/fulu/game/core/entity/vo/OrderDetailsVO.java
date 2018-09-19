package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.Order;
import lombok.Data;

@Data
public class OrderDetailsVO extends Order {

    private String serverHeadUrl;

    private String serverNickName;

    private String categoryName;

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
    
    private Boolean waitingRead;

    private Integer productAmount;
    
    private String productUnit;
}
