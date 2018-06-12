package com.fulu.game.core.service.impl;


import com.fulu.game.common.enums.PagePathEnum;
import com.fulu.game.common.enums.PushMsgTypeEnum;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.PushMsgDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.PushMsg;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.PushMsgVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.PushMsgService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.WxTemplateMsgService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private AdminService adminService;
    @Autowired
    private UserService userService;
    @Autowired
    private WxTemplateMsgService wxTemplateMsgService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;


    private Lock lock = new ReentrantLock();

    @Override
    public ICommonDao<PushMsg, Integer> getDao() {
        return pushMsgDao;
    }


    @Override
    public void push(PushMsgVO pushMsgVO) {
        Admin admin = adminService.getCurrentUser();
        pushMsgVO.setAdminId(admin.getId());
        pushMsgVO.setAdminName(admin.getName());
        pushMsgVO.setCreateTime(new Date());
        pushMsgVO.setUpdateTime(new Date());
        pushMsgVO.setHits(0L);
        pushMsgVO.setTotalNum(0);
        pushMsgVO.setSuccessNum(0);
        pushMsgVO.setIsPushed(false);
        create(pushMsgVO);
        log.info("管理员添加推送记录:pushMsgVO:{}", pushMsgVO);
        if (pushMsgVO.getTouchTime() == null) {
            log.info("实时推送微信消息执行:pushMsgVO:{};", pushMsgVO);
            exePushTask(pushMsgVO);
        }
    }


    private void exePushTask(PushMsg pushMsg) {
        log.info("开始推送微信消息:pushMsg:{};", pushMsg);
        List<Integer> userIds = new ArrayList<>();
        if (PushMsgTypeEnum.ALL_USER.getType().equals(pushMsg.getType())) {
            List<User> userList = userService.findAllNormalUser();
            for (User user : userList) {
                userIds.add(user.getId());
            }
        } else if (PushMsgTypeEnum.ASSIGN_USERID.getType().equals(pushMsg.getType())) {
            try {
                String[] userArr = pushMsg.getPushIds().split(",");
                if (userArr.length == 0) {
                    throw new ServiceErrorException("指定用户ID为空!");
                }
                for (int i = 0; i < userArr.length; i++) {
                    userIds.add(Integer.valueOf(userArr[i]));
                }
            } catch (Exception e) {
                log.error("指定用户推送错误:", e);
                throw new ServiceErrorException("指定用户输入错误!");
            }
        } else {
            throw new ServiceErrorException("推送类型指定错误!");
        }
        StringBuffer lastPage = new StringBuffer(PagePathEnum.PUSH_PAGE.getPagePath())
                .append("?redirect=")
                .append(pushMsg.getPage())
                .append("&pushId=")
                .append(pushMsg.getId());
        log.info("开始执行推送消息:userId:{};lastPage:{};pushMsg:{};", userIds, lastPage, pushMsg);
        userIds.forEach((userId) -> {
            wxTemplateMsgService.adminPushWxTemplateMsg(pushMsg.getId(), userId, lastPage.toString(), pushMsg.getContent());
        });
        pushMsg.setTotalNum(userIds.size());
        pushMsg.setIsPushed(true);
        pushMsg.setUpdateTime(new Date());
        update(pushMsg);
    }


    @Override
    public void appointPush(PushMsg pushMsg) {
        if (pushMsg.getTouchTime() == null) {
            return;
        }
        if (!pushMsg.getIsPushed() && pushMsg.getTouchTime().before(new Date())) {
            log.info("指定时间推送微信消息执行:pushMsg:{};", pushMsg);
            exePushTask(pushMsg);
        }
    }

    @Override
    public PageInfo<PushMsg> list(int pageNum, int pageSize, String orderBy) {
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "id desc";
        }
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<PushMsg> list = getDao().findAll();
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
        long bitSetVal = Long.valueOf(pushId + "" + user.getId());
        boolean flag = redisOpenService.getBitSet(RedisKeyEnum.BITSET_PUSH_MSG_HITS.generateKey(), bitSetVal);
        if (flag) {
            log.info("用户已经点击过该推送:pushId:{};userId:{};", pushId, user.getId());
            return;
        }
        redisOpenService.bitSet(RedisKeyEnum.BITSET_PUSH_MSG_HITS.generateKey(), bitSetVal);
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


}