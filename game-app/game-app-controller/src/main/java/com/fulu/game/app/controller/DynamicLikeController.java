package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.DynamicLike;
import com.fulu.game.core.entity.vo.DynamicLikeVO;
import com.fulu.game.core.service.DynamicLikeService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/8/31 11:22.
 * @Description: 点赞
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/dynamic-like")
public class DynamicLikeController  extends BaseController{

    @Autowired
    private DynamicLikeService dynamicLikeService;

    /**
     * 点赞接口
     * @return
     */
    @RequestMapping(value = "save")
    public Result save(@RequestParam( required = true) Long dynamicId) {
        DynamicLikeVO dynamicLikeVO = new DynamicLikeVO();
        dynamicLikeVO.setDynamicId(dynamicId);
        dynamicLikeService.save(dynamicLikeVO);
        return Result.success().data(dynamicLikeVO).msg("成功！");
    }


    /**
     * 获取点赞记录列表接口
     * @param pageNum
     * @param pageSize
     * @param dynamicId 动态id
     * @return
     */
    @RequestMapping(value = "list")
    public Result list(@RequestParam(required = true) Integer pageNum,
                       @RequestParam(required = true) Integer pageSize,
                       @RequestParam(required = true) Long dynamicId) {
        PageInfo<DynamicLike> page = dynamicLikeService.list( pageNum, pageSize, dynamicId);
        return Result.success().data(page);
    }


}
