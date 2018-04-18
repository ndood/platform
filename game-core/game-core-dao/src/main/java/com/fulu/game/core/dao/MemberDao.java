package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Member;

import java.util.List;
import java.util.Map;

import com.fulu.game.core.entity.vo.MemberVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统管理员表
 * @author wangbin
 * @email ${email}
 * @date 2018-04-18 15:38:16
 */
@Mapper
public interface MemberDao extends ICommonDao<Member,Integer>{


    Member findByParameter(MemberVO memberVO);

}
