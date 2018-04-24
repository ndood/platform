package com.fulu.game.core.service.impl;

import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.CashDrawsVO;
import com.fulu.game.core.service.UserService;
//import org.springframework.beans.BeanUtils;
import com.xiaoleilu.hutool.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fulu.game.core.dao.CashDrawsDao;
import com.fulu.game.core.entity.CashDraws;
import com.fulu.game.core.service.CashDrawsService;

import java.util.Date;

@Service("/cashDrawsService")
public class CashDrawsServiceImpl extends AbsCommonService<CashDraws,Integer> implements CashDrawsService {

    @Autowired
	private CashDrawsDao cashDrawsDao;
    @Autowired
    private UserService userService;

    @Override
    public ICommonDao<CashDraws, Integer> getDao() {
        return cashDrawsDao;
    }

    @Override
    public CashDraws save(CashDrawsVO cashDrawsVO){
        User user = userService.findById(cashDrawsVO.getUserId());
        CashDraws cashDraws = new CashDraws();
        BeanUtil.copyProperties(cashDrawsVO,cashDraws);

        cashDraws.setNickname(user.getNickname());
        cashDraws.setMobile(user.getMobile());
        cashDraws.setOperator("admin");
        cashDraws.setCashStatus(0);
        cashDraws.setCreateTime(new Date());
        cashDrawsDao.create(cashDraws);
        return cashDraws;
    }
	
}
