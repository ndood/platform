package com.fulu.game.play.controller;

import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Approve;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.ApproveVO;
import com.fulu.game.core.service.ApproveService;
import com.fulu.game.core.service.UserTechAuthService;
import com.xiaoleilu.hutool.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/approve")
public class ApproveController {

    @Autowired
    private ApproveService approveService;

    @Autowired
    private UserTechAuthService utaService;

    /**
     * 好友认可陪玩师技能
     * 先提前校验各种无法认可的情况
     *
     * @param techAuthId
     * @return
     */
    @PostMapping("/save")
    public Result save(@RequestParam("techAuthId") Integer techAuthId) {
        Approve approve = approveService.save(techAuthId);
        UserTechAuth userTechAuth = utaService.findById(techAuthId);
        Integer techStatus = userTechAuth.getStatus();
        Integer approveCount = userTechAuth.getApproveCount();
        Integer requireCount = approveCount < 5 ? Constant.DEFAULT_APPROVE_COUNT - approveCount : 0;
        ApproveVO approveVO = new ApproveVO();
        BeanUtil.copyProperties(approve, approveVO);
        approveVO.setTechStatus(techStatus);
        approveVO.setApproveCount(approveCount);
        approveVO.setRequireCount(requireCount);
        //todo 陪玩师认证通知
        return Result.success().data(approveVO).msg("帮助好友证实成功");
    }

}
