package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.OrderDeal;
import com.fulu.game.core.entity.OrderDealFile;
import lombok.Data;

import java.util.List;


/**
 * 
 *
 * @author yanbiao
 * @date 2018-04-26 17:51:54
 */
@Data
public class OrderDealVO  extends OrderDeal {

    List<OrderDealFile> orderDealFileList;

}
