package com.fulu.game.h5.controller.fenqile;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Advice;
import com.fulu.game.core.entity.vo.UserCommentVO;
import com.fulu.game.core.service.AdviceService;
import com.fulu.game.core.service.UserCommentService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/13 17:15
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {
    private final UserCommentService commentService;

    private AdviceService adviceService;

    @Autowired
    public UserController(UserCommentService commentService, AdviceService adviceService) {
        this.commentService = commentService;
        this.adviceService = adviceService;
    }

    /**
     * 查询陪玩师的所有评论
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数据条数
     * @param serverId 陪玩师id
     * @return 封装结果集
     */
    @RequestMapping(value = "/comment/byserver")
    public Result findDetailsComments(Integer pageNum,
                                      Integer pageSize,
                                      Integer serverId) {
        PageInfo<UserCommentVO> page = commentService.findByServerId(pageNum, pageSize, serverId);
        return Result.success().data(page);
    }


    /**
     * 提交建议
     * @param content 建议内容
     * @param contact 联系方式【电话或者QQ】
     * @param advicePicUrls 建议截图
     * @return
     */
    @PostMapping("/advice/add")
    public Result addAdvice(@RequestParam("content") String content,
                            @RequestParam(value = "contact", required = false) String contact,
                            @RequestParam(value = "advicePicUrls", required = false) String[] advicePicUrls) {
        if (content == null) {
            return Result.error().msg("请填写建议内容");
        }
        Advice advice = adviceService.addAdvice(content, contact, advicePicUrls);
        return Result.success().data(advice).msg("提交成功");
    }
}
