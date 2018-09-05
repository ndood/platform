package com.fulu.game.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Dynamic;
import com.fulu.game.core.entity.DynamicFile;
import com.fulu.game.core.entity.vo.DynamicVO;
import com.fulu.game.core.search.domain.DynamicDoc;
import com.fulu.game.core.service.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/8/30 11:39.
 * @Description: 动态控制器
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/dynamic")
public class DynamicController extends BaseController {

    @Autowired
    private DynamicService dynamicService;

//    @Autowired
//    private AccessLogService accessLogService;
//
//    @Autowired
//    private AccessLogDetailService accessLogDetailService;
//
//    @Autowired
//    private DynamicCommentService dynamicCommentService;
//
//    @Autowired
//    private DynamicFileService dynamicFileService;
//
//    @Autowired
//    private DynamicLikeService dynamicLikeService;
//
//    @Autowired
//    private RewardService rewardService;


    /**
     * 保存动态接口
     * @return
     */
    @RequestMapping(value = "save")
    public Result save(DynamicVO dynamicVO) {
        Dynamic dynamic = dynamicService.save(dynamicVO);
        return Result.success().data(dynamic).msg("成功！");
    }


    /**
     * 删除动态接口
     * @return
     */
    @RequestMapping(value = "delete")
    public Result delete(@RequestParam(required = true) Long id) {
        dynamicService.deleteDynamicById(id, true);
        return Result.success().msg("删除成功！");
    }


    /**
     * 获取动态列表接口
     * @param pageSize 每页数量
     * @param slide 0：下滑刷新；1：上划加载更多
     * @param id 上划：传客户端最小id；下滑：传客户端最大id
     * @param type 动态页tab类型（1：精选；2：关注）
     * @return
     */
    @RequestMapping(value = "list")
    public Result list(@RequestParam(value = "pageSize", required = false,defaultValue = "10") Integer pageSize,
                       @RequestParam(value = "slide", required = false,defaultValue = "0") Integer slide,
                       @RequestParam(value = "id", required = false, defaultValue = "0") Integer id,
                       @RequestParam(value = "type", required = false, defaultValue = "1") Integer type) {
        Page<DynamicDoc> page = dynamicService.list(pageSize, slide, id, type);
        return Result.success().data(page);
    }


    /**
     * 获取用户动态列表接口
     * @param pageSize 每页数量
     * @param slide 0：下滑刷新；1：上划加载更多
     * @param id 上划：传客户端最小id；下滑：传客户端最大id
     * @param userId 非必传，不传查用户自己动态，传了查其他用户动态
     * @return
     */
    @RequestMapping(value = "user-dynamic/list")
    public Result userDynamicList(@RequestParam(value = "pageSize", required = false,defaultValue = "10") Integer pageSize,
                       @RequestParam(value = "slide", required = false,defaultValue = "0") Integer slide,
                       @RequestParam(value = "id", required = false, defaultValue = "0") Integer id,
                       @RequestParam( required = false) Integer userId) {
        Page<DynamicDoc> page = dynamicService.userDynamicList(pageSize, slide, id, userId);
        return Result.success().data(page);
    }

    /**
     * 获取动态详情
     * @param id
     * @return
     */
    @RequestMapping(value = "detail")
    public Result detail(@RequestParam( required = true) Long id) {
        DynamicDoc dynamicDoc = dynamicService.getDynamicDocById(id);
        return Result.success().data(dynamicDoc);
    }
}
