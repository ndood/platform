package com.fulu.game.core.service;

import com.fulu.game.core.entity.CashDraws;
import com.fulu.game.core.entity.vo.CashDrawsVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yanbiao
 * @date 2018-04-24 16:45:40
 */

@Transactional
public interface CashDrawsService extends ICommonService<CashDraws,Integer>{

    CashDraws save(CashDrawsVO cashDrawsVO);
}
