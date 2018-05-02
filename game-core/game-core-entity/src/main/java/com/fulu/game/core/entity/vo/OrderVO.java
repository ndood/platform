package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.Product;
import lombok.Data;

import java.util.Date;


/**
 * 订单表
 *
 * @author yanbiao
 * @date 2018-04-25 18:27:54
 */
@Data
public class OrderVO  extends Order {

    private Product product ;

    private String serverHeadUrl;

    private String statusStr;

    private String categoryIcon;

    private Date endTime;

    private Date startTime;

    /**
     * 订单状态列表
     */
    private Integer[] statusList;

}
