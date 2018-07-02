package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Advice;
import com.fulu.game.core.entity.vo.AdviceVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户建议表
 *
 * @author yanbiao
 * @date 2018-07-02 11:03:20
 */
@Mapper
public interface AdviceDao extends ICommonDao<Advice, Integer> {

    List<Advice> findByParameter(AdviceVO adviceVO);

}
