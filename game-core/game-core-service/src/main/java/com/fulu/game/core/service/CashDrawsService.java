package com.fulu.game.core.service;

import com.fulu.game.core.entity.CashDraws;
import com.fulu.game.core.entity.vo.CashDrawsVO;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yanbiao
 * @date 2018-04-24 16:45:40
 */

@Transactional
public interface CashDrawsService extends ICommonService<CashDraws,Integer>{

    CashDrawsVO save(CashDrawsVO cashDrawsVO);

    PageInfo<CashDrawsVO> list(CashDrawsVO cashDrawsVO,Integer pageNum, Integer pageSize);

    PageInfo<CashDrawsVO> list(CashDrawsVO cashDrawsVO);

    CashDraws draw(Integer cashId, String comment);

    boolean refuse(Integer cashId, String comment);
}
