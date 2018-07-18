package com.fulu.game.core.dao;

import com.fulu.game.core.entity.OrderConsult;
import com.fulu.game.core.entity.vo.OrderConsultVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-07-18 15:40:34
 */
@Mapper
public interface OrderConsultDao extends ICommonDao<OrderConsult,Integer>{

    List<OrderConsult> findByParameter(OrderConsultVO orderConsultVO);

}
