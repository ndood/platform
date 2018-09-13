package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.DynamicLike;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.DynamicPushMsgVO;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.DynamicPushMsgDao;
import com.fulu.game.core.entity.DynamicPushMsg;
import com.fulu.game.core.service.DynamicPushMsgService;

import java.util.List;


@Service
public class DynamicPushMsgServiceImpl extends AbsCommonService<DynamicPushMsg,Integer> implements DynamicPushMsgService {

    @Autowired
	private DynamicPushMsgDao dynamicPushMsgDao;

    @Autowired
    private UserService userService;



    @Override
    public ICommonDao<DynamicPushMsg, Integer> getDao() {
        return dynamicPushMsgDao;
    }

    /**
     * 获取消息推送记录接口
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<DynamicPushMsg> list(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        DynamicPushMsgVO dynamicPushMsgVO = new DynamicPushMsgVO();
        User user = userService.getCurrentUser();
        dynamicPushMsgVO.setToUserId(user.getId());
        List<DynamicPushMsg> list = dynamicPushMsgDao.findByParameter(dynamicPushMsgVO);
        return new PageInfo<>(list);
    }

    /**
     * 获取最新一条动态push消息
     *
     * @return
     */
    @Override
    public DynamicPushMsg newDynamicPushMsg() {
        DynamicPushMsgVO dynamicPushMsgVO = new DynamicPushMsgVO();
        User user = userService.getCurrentUser();
        dynamicPushMsgVO.setToUserId(user.getId());
        DynamicPushMsg dynamicPushMsg = dynamicPushMsgDao.newDynamicPushMsg(dynamicPushMsgVO);
        return dynamicPushMsg;
    }
}
