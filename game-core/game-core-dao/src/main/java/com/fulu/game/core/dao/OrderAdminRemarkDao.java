package com.fulu.game.core.dao;

import com.fulu.game.core.entity.OrderAdminRemark;
import com.fulu.game.core.entity.vo.OrderAdminRemarkVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author jaydee.Deng
 * @email ${email}
 * @date 2018-09-17 16:42:51
 */
@Mapper
public interface OrderAdminRemarkDao extends ICommonDao<OrderAdminRemark,Integer>{

    List<OrderAdminRemark> findByParameter(OrderAdminRemarkVO orderAdminRemarkVO);

}
