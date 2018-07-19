package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.OrderStatusDetails;
import lombok.Data;


/**
 * 
 *
 * @author wangbin
 * @date 2018-07-18 12:04:41
 */
@Data
public class OrderStatusDetailsVO  extends OrderStatusDetails {

    /**
     * 订单状态文字描述
     */
    String orderStatusMsg;

}
