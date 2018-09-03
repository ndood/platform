package com.fulu.game.core.dao;

import com.fulu.game.core.entity.VirtualProductAttach;
import com.fulu.game.core.entity.vo.VirtualProductAttachVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 虚拟商品附件表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-30 15:05:30
 */
@Mapper
public interface VirtualProductAttachDao extends ICommonDao<VirtualProductAttach, Integer> {

    List<VirtualProductAttach> findByParameter(VirtualProductAttachVO virtualProductAttachVO);


    List<VirtualProductAttach> findByOrderProIdUserId(@Param(value = "userId") Integer userId, @Param(value = "productId") Integer productId);

    List<VirtualProductAttachVO> findDetailByVo(VirtualProductAttachVO virtualProductAttachVO);

    public int deleteByVirtualProductId(int virtualProductId);
}
