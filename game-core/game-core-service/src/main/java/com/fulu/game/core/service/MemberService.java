package com.fulu.game.core.service;

import com.fulu.game.core.entity.Member;
import com.fulu.game.core.entity.vo.MemberVO;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * 系统管理员表
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-04-18 15:38:16
 */
public interface MemberService extends ICommonService<Member,Integer>{

    PageInfo<Member> list(MemberVO memberVO,Integer pageNum, Integer pageSize);
    Member findByUsername(String username);
}
