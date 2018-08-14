package com.fulu.game.core.dao;

import com.fulu.game.core.entity.FenqileReconciliation;
import com.fulu.game.core.entity.vo.FenqileReconciliationVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 分期乐对账表
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-14 18:24:00
 */
@Mapper
public interface FenqileReconciliationDao extends ICommonDao<FenqileReconciliation,Integer>{

    List<FenqileReconciliation> findByParameter(FenqileReconciliationVO fenqileReconciliationVO);

}
