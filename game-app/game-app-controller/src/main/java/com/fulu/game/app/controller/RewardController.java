package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Dynamic;
import com.fulu.game.core.entity.Reward;
import com.fulu.game.core.entity.vo.DynamicVO;
import com.fulu.game.core.entity.vo.RewardVO;
import com.fulu.game.core.entity.vo.UserFriendVO;
import com.fulu.game.core.service.RewardService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/8/30 18:19.
 * @Description: 打赏控制器
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/reward")
public class RewardController extends BaseController {

    @Autowired
    private RewardService rewardService;

    /**
     * 打赏接口
     * @return
     */
    @RequestMapping(value = "save")
    public Result save(RewardVO rewardVO) {
        rewardService.save(rewardVO);
        return Result.success().data(rewardVO).msg("成功！");
    }

    /**
     * 获取打赏记录列表接口
     * @param pageNum
     * @param pageSize
     * @param resourceId 动打赏来源id
     * @param resourceType 打赏来源类型（1：动态打赏；）
     * @return
     */
    @RequestMapping(value = "list")
    public Result list(@RequestParam(required = true) Integer pageNum,
                       @RequestParam(required = true) Integer pageSize,
                       @RequestParam(required = true) Integer resourceId,
                       @RequestParam(required = false, defaultValue = "1") Integer resourceType) {
        PageInfo<Reward> page = rewardService.list(pageNum, pageSize, resourceId, resourceType);
        return Result.success().data(page);
    }
}
