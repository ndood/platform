package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Dynamic;
import com.fulu.game.core.entity.vo.DynamicVO;
import com.fulu.game.core.service.DynamicService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author: shijiaoyun.
 * @Date: 2018/9/4 16:20.
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/dynamic")
public class DynamicController extends BaseController {
    @Autowired
    private DynamicService dynamicService;


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
     * 获取动态列表接口
     * @param pageNum 当前页
     * @param pageSize 每页数量
     * @return
     */
    @RequestMapping(value = "list")
    public Result list(@RequestParam Integer pageNum,
                       @RequestParam Integer pageSize,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String startTime,
                       @RequestParam(required = false) String endTime) {
        PageInfo<DynamicVO> page = dynamicService.adminList(pageNum, pageSize, keyword, startTime, endTime);
        return Result.success().data(page);
    }

    /**
     * 删除动态接口
     * @return
     */
    @RequestMapping(value = "delete")
    public Result delete(@RequestParam(required = true) Long id) {
        dynamicService.deleteDynamicById(id, false);
        return Result.success().msg("删除成功！");
    }




}
