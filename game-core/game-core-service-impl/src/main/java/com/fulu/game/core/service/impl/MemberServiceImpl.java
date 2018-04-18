package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.MemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.fulu.game.core.dao.MemberDao;
import com.fulu.game.core.entity.Member;
import com.fulu.game.core.service.MemberService;



@Service
public class MemberServiceImpl extends AbsCommonService<Member,Integer> implements MemberService {

    @Autowired
	private MemberDao memberDao;



    @Override
    public ICommonDao<Member, Integer> getDao() {
        return memberDao;
    }

    @Override
    public Member findByUsername(String username) {
        MemberVO memberVO = new MemberVO();
        memberVO.setUsername(username);
        return memberDao.findByParameter(memberVO);
    }
}
