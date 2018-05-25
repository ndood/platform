package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Approve;
import com.fulu.game.core.service.ApproveService;
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

    /**
     * 好友能力认可
     * 返回陪玩师技能的认证状态
     *
     * @param techAuthId
     * @return
     */
    @PostMapping("/save")
    public Result save(@RequestParam("techAuthId") Integer techAuthId) {
        Approve approve = approveService.save(techAuthId);
        return Result.success().data(approve).msg("帮助好友证实成功");
    }

}
