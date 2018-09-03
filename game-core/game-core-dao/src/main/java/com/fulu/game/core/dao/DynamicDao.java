package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Dynamic;
import com.fulu.game.core.entity.vo.DynamicVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 动态表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-08-30 10:31:41
 */
@Mapper
public interface DynamicDao extends ICommonDao<Dynamic,Long>{

    List<Dynamic> findByParameter(DynamicVO dynamicVO);

}
