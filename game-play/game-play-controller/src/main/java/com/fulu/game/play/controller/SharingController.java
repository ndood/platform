package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Sharing;
import com.fulu.game.core.entity.vo.SharingVO;
import com.fulu.game.core.service.SharingService;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/sharing")
public class SharingController extends BaseController {

    @Autowired
    private SharingService sharingService;

    @PostMapping("/get")
    public Result get(@RequestParam("shareType") Integer shareType,
                      @RequestParam("gender") Integer gender) {
        SharingVO queryVO = new SharingVO();
        queryVO.setShareType(shareType);
        queryVO.setGender(gender);
        queryVO.setStatus(true);
        List<Sharing> sharingList = sharingService.findByParam(queryVO);
        if (!CollectionUtil.isEmpty(sharingList)) {
            return Result.success().data(sharingList.get(0)).msg("查询成功！");
        } else {
            return Result.error().msg("文案尚未配置或文案弃用！");
        }
    }
}
