package com.fulu.game.app.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Dynamic;
import com.fulu.game.core.entity.DynamicComment;
import com.fulu.game.core.entity.vo.DynamicCommentVO;
import com.fulu.game.core.entity.vo.DynamicVO;
import com.fulu.game.core.service.DynamicCommentService;
import com.fulu.game.core.service.DynamicService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/8/31 9:45.
 * @Description: 动态评论
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/dynamic-comment")
public class DynamicCommentController extends BaseController {

    @Autowired
    private DynamicCommentService dynamicCommentService;

    /**
     * 评论接口
     * @return
     */
    @RequestMapping(value = "save")
    public Result save(DynamicCommentVO dynamicCommentVO) {
        dynamicCommentService.save(dynamicCommentVO);
        return Result.success().data(dynamicCommentVO).msg("评论成功！");
    }

    /**
     * 删除评论接口
     * @return
     */
    @RequestMapping(value = "delete")
    public Result delete(@RequestParam(required = true) Integer id) {
        dynamicCommentService.deleteById(id);
        return Result.success().msg("删除成功！");
    }

    /**
     * 获取评论记录列表接口
     * @param pageNum
     * @param pageSize
     * @param dynamicId 动态id
     * @return
     */
    @RequestMapping(value = "list")
    public Result list(@RequestParam(required = true) Integer pageNum,
                       @RequestParam(required = true) Integer pageSize,
                       @RequestParam(required = true) Integer dynamicId) {
        PageInfo<DynamicCommentVO> page = dynamicCommentService.list( pageNum, pageSize, dynamicId);
        return Result.success().data(page);
    }

}
