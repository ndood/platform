package com.fulu.game.core.service.impl;

import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.MemberDao;
import com.fulu.game.core.entity.vo.MemberVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.fulu.game.core.entity.Member;
import com.fulu.game.core.service.MemberService;

@Service("memberService")
public class MemberServiceImpl extends AbsCommonService<Member,Integer> implements MemberService {

    @Autowired
    public MemberDao memberDao;

    @Override
    public ICommonDao<Member, Integer> getDao() {
        return memberDao;
    }

    @Override
    public Member findByUsername(String username) {
        MemberVO memberVO = new MemberVO();
        memberVO.setUsername(username);
        return memberDao.findByParameter(memberVO).get(0);
    }

    @Override
    public PageInfo<Member> list(MemberVO memberVO,Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize,memberVO.getOrderBy());
        List<Member> list = memberDao.findByParameter(memberVO);
        return new PageInfo(list);
    }
}
