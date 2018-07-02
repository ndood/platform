package com.fulu.game.core.service.impl;

import com.fulu.game.common.enums.StatusEnum;
import com.fulu.game.core.dao.AdviceDao;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.Advice;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.AdviceVO;
import com.fulu.game.core.service.AdviceFileService;
import com.fulu.game.core.service.AdviceService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AdviceServiceImpl extends AbsCommonService<Advice, Integer> implements AdviceService {

    @Autowired
    private AdviceDao adviceDao;
    @Autowired
    private UserService userService;
    @Autowired
    private AdviceFileService adviceFileService;

    @Override
    public ICommonDao<Advice, Integer> getDao() {
        return adviceDao;
    }

    public Advice addAdvice(String content, String contact, String[] advicePicUrls) {
        log.info("===调用用户提建议接口===");
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
        Integer adviceId = advice.getId();
        adviceFileService.save(advicePicUrls, adviceId);
        return advice;
    }

    public PageInfo<AdviceVO> list(Integer pageNum, Integer pageSize, AdviceVO adviceVO) {
        PageHelper.startPage(pageNum, pageSize, "create_time desc");
        List<AdviceVO> list = adviceDao.listByParam(adviceVO);
        return new PageInfo<>(list);
    }
}
