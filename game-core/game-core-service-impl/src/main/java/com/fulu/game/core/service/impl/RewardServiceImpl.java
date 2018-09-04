package com.fulu.game.core.service.impl;


import com.fulu.game.common.exception.ParamsException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.VirtualProductOrder;
import com.fulu.game.core.entity.vo.RewardVO;
import com.fulu.game.core.entity.vo.UserFriendVO;
import com.fulu.game.core.search.component.DynamicSearchComponent;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.RewardDao;
import com.fulu.game.core.entity.Reward;

import java.util.List;

@Slf4j
@Service("rewardService")
public class RewardServiceImpl extends AbsCommonService<Reward, Long> implements RewardService {

    @Autowired
    private RewardDao rewardDao;

    @Autowired
    private VirtualProductOrderService virtualProductOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private DynamicService dynamicService;

    @Override
    public ICommonDao<Reward, Long> getDao() {
        return rewardDao;
    }

    /**
     * 保存打赏记录
     * 生存订单号，插入订单表，然后在插入打赏记录表
     *
     * @param rewardVO
     * @return
     */
    @Override
    public void save(RewardVO rewardVO) {
        if (rewardVO == null) {
            throw new ParamsException(ParamsException.ExceptionCode.PARAM_NULL_EXCEPTION);
        }
        User user = userService.getCurrentUser();
        log.info("打赏记录：打赏用户Id：{}，接收奖励用户id：{}，礼物id：{}", user.getId(), rewardVO.getToUserId(), rewardVO.getGiftId());
        VirtualProductOrder virtualProductOrder = virtualProductOrderService.sendGift(user.getId(), rewardVO.getToUserId().intValue(), rewardVO.getGiftId().intValue());
        if(virtualProductOrder != null ){
            rewardVO.setOrderNo(virtualProductOrder.getOrderNo());
            create(rewardVO);
            //修改动态点赞次数
            dynamicService.updateIndexFilesById(rewardVO.getResourceId(),true, 0, 0,false);
        } else {
            log.error("打赏记录：打赏用户Id：{}，接收奖励用户id：{}，礼物id：{}，调用虚拟商品订单接口失败", user.getId(), rewardVO.getToUserId(), rewardVO.getGiftId());
        }
    }

    /**
     * 获取打赏记录列表
     *
     * @param pageNum
     * @param pageSize
     * @param dynamicId
     * @return
     */
    @Override
    public PageInfo<Reward> list(Integer pageNum, Integer pageSize, Long dynamicId) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        RewardVO rewardVO = new RewardVO();
        rewardVO.setResourceId(dynamicId);
        List<Reward> list = rewardDao.findByParameter(rewardVO);
        return new PageInfo<>(list);
    }
}
