package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.Product;
import lombok.Data;


/**
 * 订单表
 *
 * @author yanbiao
 * @date 2018-04-25 18:27:54
 */
@Data
public class OrderVO  extends Order {

    Product product ;
}
