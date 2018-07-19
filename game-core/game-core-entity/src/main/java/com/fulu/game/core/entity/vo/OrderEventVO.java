package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.OrderEvent;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @author wangbin
 * @date 2018-07-18 15:40:34
 */
@Data
public class OrderEventVO extends OrderEvent {

    private Integer identity;

    private String categoryName;
    //倒计时
    private Long countDown;
    //用户昵称
    private String nickname;
    //头像
    private String headUrl;
    //当前订单状态
    private Integer currentOrderStatus;
    //当前状态中文
    private String currentOrderStatusStr;

    //
    List<OrderDealVO> orderDealList = new ArrayList<>();

}
