package com.fulu.game.core.dao;

import com.fulu.game.core.entity.ProductTop;
import com.fulu.game.core.entity.vo.ProductTopVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 商品置顶表
 * @author wangbin
 * @email ${email}
 * @date 2018-06-07 15:28:32
 */
@Mapper
public interface ProductTopDao extends ICommonDao<ProductTop,Integer>{

    List<ProductTop> findByParameter(ProductTopVO productTopVO);

}
