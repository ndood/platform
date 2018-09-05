package com.fulu.game.core.dao;

import com.fulu.game.core.entity.VirtualDetails;
import com.fulu.game.core.entity.vo.VirtualDetailsVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 虚拟币和魅力值详情流水表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-30 15:26:37
 */
@Mapper
public interface VirtualDetailsDao extends ICommonDao<VirtualDetails, Integer> {

    List<VirtualDetails> findByParameter(VirtualDetailsVO virtualDetailsVO);

}
