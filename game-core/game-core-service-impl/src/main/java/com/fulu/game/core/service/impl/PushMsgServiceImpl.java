package com.fulu.game.core.service.impl;


import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.enums.PushMsgJumpTypeEnum;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.PushMsgDao;
import com.fulu.game.core.entity.PushMsg;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.PushMsgVO;
import com.fulu.game.core.service.PushMsgService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Service
@Slf4j
public class PushMsgServiceImpl extends AbsCommonService<PushMsg, Integer> implements PushMsgService {

    @Autowired
    private PushMsgDao pushMsgDao;

    @Autowired
    private UserService userService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;


    private Lock lock = new ReentrantLock();

    @Override
    public ICommonDao<PushMsg, Integer> getDao() {
        return pushMsgDao;
    }


    @Override
    public PageInfo<PushMsg> list(Integer pageNum, Integer pageSize, Integer platform, String orderBy) {
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "id desc";
        }
        PageHelper.startPage(pageNum, pageSize, orderBy);
        PushMsgVO pushMsgVO = new PushMsgVO();
        if (platform != null) {
            pushMsgVO.setPlatform(platform);
        }
        List<PushMsg> list = pushMsgDao.findByParameter(pushMsgVO);
        PageInfo page = new PageInfo(list);
        return page;
    }


    @Override
    public List<PushMsg> findTodayNotPushMsg() {
        Date beginDate = DateUtil.beginOfDay(new Date());
        Date endDate = DateUtil.endOfDay(new Date());
        List<PushMsg> pushMsgs = pushMsgDao.findNotPushMsgByTouchTime(beginDate, endDate);
        return pushMsgs;
    }


    @Override
    public void hitsStatistics(int pushId) {
        User user = userService.getCurrentUser();
        long bitSetVal = Long.valueOf(user.getId());
        boolean flag = redisOpenService.getBitSet(RedisKeyEnum.BITSET_PUSH_MSG_HITS.generateKey(pushId), bitSetVal);
        if (flag) {
            log.info("用户已经点击过该推送:pushId:{};userId:{};", pushId, user.getId());
            return;
        }
        redisOpenService.bitSet(RedisKeyEnum.BITSET_PUSH_MSG_HITS.generateKey(pushId), bitSetVal);
        lock.lock();
        try {
            PushMsg pushMsg = findById(pushId);
            pushMsg.setHits(pushMsg.getHits() + 1);
            update(pushMsg);
            log.info("用户点击该推送:pushId:{};userId:{};", pushId, user.getId());
        } finally {
            lock.unlock();
        }

    }

    @Override
    public PageInfo<PushMsg> officialNoticeList(Integer pageNum, Integer pageSize) {
        String orderBy = "touch_time DESC";
        PageHelper.startPage(pageNum, pageSize, orderBy);

        PushMsgVO pushMsgVO = new PushMsgVO();
        pushMsgVO.setPlatform(PlatformEcoEnum.APP.getType());
        pushMsgVO.setJumpType(PushMsgJumpTypeEnum.OFFICIAL_NOTE.getType());
        List<PushMsg> noticeList = pushMsgDao.findByParameter(pushMsgVO);
        return new PageInfo<>(noticeList);
    }

    @Override
    public PushMsg newOfficialNotice() {
        return pushMsgDao.newOfficialNotice();
    }
}
