package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.core.entity.Banner;
import com.fulu.game.core.entity.vo.BannerVO;
import com.fulu.game.core.service.BannerService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/api/v1/banner")
public class BannerController extends BaseController{

    @Autowired
    private BannerService bannerService;
    @Autowired
    private OssUtil ossUtil;
    /**
     * @param bannerVO
     * @return
     */
    @PostMapping("/save")
    public Result save(@Valid BannerVO bannerVO) {
        bannerVO.setPicUrl(ossUtil.activateOssFile(bannerVO.getPicUrl()));
        Banner banner = bannerService.save(bannerVO);
        return Result.success().data(banner).msg("添加成功");
    }

    @PostMapping("/update")
    public Result update(@Valid BannerVO bannerVO) {
        bannerVO.setPicUrl(ossUtil.activateOssFile(bannerVO.getPicUrl()));
        Banner banner = bannerService.update(bannerVO);
        return Result.success().data(banner).msg("修改成功");
    }

    /**
     * 启用和禁用
     *
     * @return
     */
    @PostMapping("/disable")
    public Result disable(@RequestParam("id") Integer id,
                          @RequestParam("disable") Boolean disable) {
        Banner banner = bannerService.disable(id, disable);
        return Result.success().data(banner).msg("操作成功");
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam("id") Integer id) {
        bannerService.delete(id);
        return Result.success().msg("删除成功");
    }

    @PostMapping("/list")
    public Result list(@RequestParam("pageSize") Integer pageSize, @RequestParam("pageNum") Integer pageNum) {
        PageInfo<Banner> bannerList = bannerService.list(pageNum, pageSize);
        return Result.success().data(bannerList).msg("查询成功");
    }

}
