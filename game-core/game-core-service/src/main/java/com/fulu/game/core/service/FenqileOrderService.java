package com.fulu.game.core.service;

import com.fulu.game.core.entity.FenqileOrder;
import com.fulu.game.core.entity.vo.FenqileOrderVO;
import com.fulu.game.core.entity.vo.searchVO.FenqileOrderSearchVO;
import com.github.pagehelper.PageInfo;


/**
 * 分期乐订单拓展表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-14 18:19:17
 */
public interface FenqileOrderService extends ICommonService<FenqileOrder, Integer> {

    PageInfo<FenqileOrderVO> list(Integer pageNum,
                                  Integer pageSize,
                                  String orderBy,
                                  FenqileOrderSearchVO searchVO);

    FenqileOrderVO getTotalReconAmount(FenqileOrderSearchVO searchVO);
}
