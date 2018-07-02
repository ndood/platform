package com.fulu.game.core.service.impl;

import com.fulu.game.common.enums.StatusEnum;
import com.fulu.game.core.dao.AdviceDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Advice;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.AdviceService;
import com.fulu.game.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AdviceServiceImpl extends AbsCommonService<Advice, Integer> implements AdviceService {

    @Autowired
    private AdviceDao adviceDao;
    @Autowired
    private UserService userService;

    @Override
    public ICommonDao<Advice, Integer> getDao() {
        return adviceDao;
    }

    public void addAdvice(String content, String contact, String[] advicePicUrls) {
        User user = userService.getCurrentUser();
        Advice advice = new Advice();
        advice.setCreateTime(new Date());
        advice.setUpdateTime(new Date());
        advice.setContent(content);
        advice.setContact(contact);
        advice.setNickname(user.getNickname());
        advice.setUserId(user.getId());
        advice.setStatus(StatusEnum.ADVICE_WAIT.getType());
        adviceDao.create(advice);
        int adviceId = advice.getId();


    }

}
