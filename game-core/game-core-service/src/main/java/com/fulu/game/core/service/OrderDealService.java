package com.fulu.game.core.service;

import com.fulu.game.core.entity.OrderDeal;
import com.fulu.game.core.entity.vo.OrderDealVO;


/**
 * 
 * 
 * @author yanbiao
 * @email ${email}
 * @date 2018-04-26 17:51:54
 */
public interface OrderDealService extends ICommonService<OrderDeal,Integer>{


     void create(String orderNo,Integer userId,Integer type,String remark,String ...fileUrls);

     OrderDealVO findByUserAndOrderNo(Integer userId, String orderNo);
}
