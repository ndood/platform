package com.fulu.game.core.service;

import com.fulu.game.core.entity.VirtualDetails;
import com.fulu.game.core.entity.vo.VirtualDetailsVO;
import com.github.pagehelper.PageInfo;

import java.util.List;


/**
 * 虚拟币和魅力值详情流水表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-30 15:26:37
 */
public interface VirtualDetailsService extends ICommonService<VirtualDetails, Integer> {

    PageInfo<VirtualDetails> findByParameterWithPage(VirtualDetailsVO virtualDetailsVO, Integer pageSize, Integer pageNum , String orderBy);
    
}