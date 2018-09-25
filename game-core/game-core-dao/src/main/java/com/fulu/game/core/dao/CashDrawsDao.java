package com.fulu.game.core.dao;

import com.fulu.game.core.entity.CashDraws;
import com.fulu.game.core.entity.vo.CashDrawsVO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yanbiao
 * @date 2018-04-24 16:45:40
 */
@Mapper
public interface CashDrawsDao extends ICommonDao<CashDraws, Integer> {

    List<CashDraws> findByParameter(CashDrawsVO cashDrawsVO);

    List<CashDraws> findListOrderByCreateTime(CashDrawsVO cashDrawsVO);

    List<CashDrawsVO> findDetailByParameter(CashDrawsVO cashDrawsVO);

    CashDraws findByCashNo(String cashNo);

    /**
     * 获取未提现金额的汇总（运营未处理，财务未处理）
     *
     * @param cashDrawsVO 查询VO
     * @return 汇总金额
     */
    BigDecimal findUnCashDrawsSum(CashDrawsVO cashDrawsVO);
}
