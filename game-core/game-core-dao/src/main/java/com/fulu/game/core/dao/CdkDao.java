package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Cdk;
import com.fulu.game.core.entity.vo.CdkVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * cdk记录表
 *
 * @author yanbiao
 * @date 2018-06-13 15:33:53
 */
@Mapper
public interface CdkDao extends ICommonDao<Cdk, Integer> {

    List<Cdk> findByParameter(CdkVO cdkVO);

}
