package com.fulu.game.core.service;

import com.fulu.game.core.entity.OrderDealFile;

import java.util.List;


/**
 * 订单处理文件表
 * 
 * @author yanbiao
 * @email ${email}
 * @date 2018-04-26 17:51:54
 */
public interface OrderDealFileService extends ICommonService<OrderDealFile,Integer>{

     List<OrderDealFile> findByOrderDeal(Integer orderDealId);
}
