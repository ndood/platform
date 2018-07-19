package com.fulu.game.core.dao;

import com.fulu.game.core.entity.ArbitrationDetails;
import com.fulu.game.core.entity.vo.ArbitrationDetailsVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 仲裁结果流水表
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-07-19 12:23:30
 */
@Mapper
public interface ArbitrationDetailsDao extends ICommonDao<ArbitrationDetails,Integer>{

    List<ArbitrationDetails> findByParameter(ArbitrationDetailsVO arbitrationDetailsVO);

}
