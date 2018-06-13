package com.fulu.game.core.dao;

import com.fulu.game.core.entity.CdkGroup;
import com.fulu.game.core.entity.vo.CdkGroupVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * cdk批次表
 *
 * @author yanbiao
 * @date 2018-06-13 15:34:02
 */
@Mapper
public interface CdkGroupDao extends ICommonDao<CdkGroup, Integer> {

    List<CdkGroup> findByParameter(CdkGroupVO cdkGroupVO);

}
