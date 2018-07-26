package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.Order;
import lombok.Data;

@Data
public class PointOrderDetailsVO extends Order{

    private String userHeadUrl;

    private String userNickName;

    private String serverHeadUrl;

    private String serverNickName;

    private String categoryName;

    private String categoryIcon;

    private String accountInfo;

    private String orderChoice;

    private String statusStr;

    //订单状态描述
    private String statusNote;

    private Long countDown;

    private Integer productId;

    private Integer identity;

    private Integer commentScore;

    private String commentContent;


}
