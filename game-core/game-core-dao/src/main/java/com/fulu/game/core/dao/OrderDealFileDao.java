package com.fulu.game.core.dao;

import com.fulu.game.core.entity.OrderDealFile;
import com.fulu.game.core.entity.vo.OrderDealFileVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 订单处理文件表
 * @author yanbiao
 * @email ${email}
 * @date 2018-04-26 17:51:54
 */
@Mapper
public interface OrderDealFileDao extends ICommonDao<OrderDealFile,Integer>{

    List<OrderDealFile> findByParameter(OrderDealFileVO orderDealFileVO);

}
