package com.fulu.game.core.dao;

import com.fulu.game.core.entity.FenqileOrder;
import com.fulu.game.core.entity.vo.FenqileOrderVO;
import com.fulu.game.core.entity.vo.searchVO.FenqileOrderSearchVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 分期乐订单拓展表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-14 18:19:17
 */
@Mapper
public interface FenqileOrderDao extends ICommonDao<FenqileOrder, Integer> {

    List<FenqileOrder> findByParameter(FenqileOrderVO fenqileOrderVO);

    List<FenqileOrderVO> list(FenqileOrderSearchVO searchVO);

    FenqileOrderVO getTotalReconAmount(FenqileOrderSearchVO searchVO);
}
