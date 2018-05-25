package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Banner;
import com.fulu.game.core.entity.Sharing;
import com.fulu.game.core.entity.vo.BannerVO;
import com.fulu.game.core.entity.vo.SharingVO;
import com.fulu.game.core.service.BannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;
    @PostMapping("/list")
    public Result list(@RequestParam(value = "redirectType",required = false) Integer redirectType) {
        //todo 前端查询条件是什么？
        BannerVO bannerVO = new BannerVO();
        bannerVO.setRedirectType(redirectType);
        bannerVO.setDisable(true);
        List<Banner> bannerList = bannerService.findByParam(bannerVO);
        return Result.success().data(bannerList).msg("查询成功");
    }
}
