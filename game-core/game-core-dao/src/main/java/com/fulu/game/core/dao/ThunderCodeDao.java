package com.fulu.game.core.dao;

import com.fulu.game.core.entity.ThunderCode;
import com.fulu.game.core.entity.vo.ThunderCodeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 迅雷活动兑换码
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-10-12 09:50:54
 */
@Mapper
public interface ThunderCodeDao extends ICommonDao<ThunderCode, Integer> {

    List<ThunderCode> findByParameter(ThunderCodeVO thunderCodeVO);

}
