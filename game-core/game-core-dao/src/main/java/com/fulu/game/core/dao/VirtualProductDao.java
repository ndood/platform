package com.fulu.game.core.dao;

import com.fulu.game.core.entity.VirtualProduct;
import com.fulu.game.core.entity.vo.VirtualProductVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 虚拟商品表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-30 10:01:57
 */
@Mapper
public interface VirtualProductDao extends ICommonDao<VirtualProduct, Integer> {

    List<VirtualProduct> findByParameter(VirtualProductVO virtualProductVO);

    List<VirtualProduct> findAllGift();
    
    List<VirtualProductVO> findByVirtualProductVo(VirtualProductVO virtualProductVO);

    VirtualProduct findByOrderNo(String orderNo);
}
