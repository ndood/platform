package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.OrderProduct;
import com.fulu.game.core.entity.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 订单表
 *
 * @author yanbiao
 * @date 2018-04-25 18:27:54
 */
@Data
public class OrderVO  extends Order {

    private OrderProduct orderProduct;

    private String categoryName;

    private String serverHeadUrl;

    private String serverNickName;

    private BigDecimal serverScoreAvg;

    private Integer serverAge;

    private Integer serverGender;

    private String serverCity;


    private String userHeadUrl;

    private String userNickName;

    private String statusStr;

    private String categoryIcon;

    private Date endTime;

    private Date startTime;

    /**
     * 订单状态列表
     */
    private Integer[] statusList;

}
