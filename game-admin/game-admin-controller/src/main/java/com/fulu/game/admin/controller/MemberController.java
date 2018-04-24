package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Member;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.MemberVO;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.service.MemberService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/api/v1/member")
public class MemberController extends BaseController{

    @Autowired
    private UserService userService;
    @Autowired
    private MemberService memberService;

    /**
     * 查询-管理员-列表
     * @param memberVO
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/listMember")
    public Result listMember(@ModelAttribute MemberVO memberVO,
                             @RequestParam("pageNum") Integer pageNum,
                             @RequestParam("pageSize") Integer pageSize){
        PageInfo<Member> memberList = memberService.list(memberVO,pageNum,pageSize);
        return Result.success().data(memberList);
    }

    /**
     * 查询-用户-列表
     * @param userVO
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/listUser")
    public Result listUser(@ModelAttribute UserVO userVO,Integer pageNum, Integer pageSize){
        PageInfo<User> userList = userService.list(userVO,pageNum,pageSize);
        return Result.success().data(userList);
    }


    /**
     * 单个用户-封禁
     * 封禁操作后期建议写入日志
     * @param id
     * @return
     */
    @RequestMapping("/lock")
    public Result lock(@RequestParam("userId") int id){
        userService.lock(id);
        log.info("user "+ id + " is locked at " + new Date());
        return Result.success().msg("lock success");
    }

    /**
     * 单个用户-解封
     * 解禁操作后期建议写入日志并通知用户
     * @param id
     * @return
     */
    @RequestMapping("/")
    public Result unlock(@RequestParam("userId") int id){
        userService.unlock(id);
        log.info("unlock user "+ id + " at " + new Date());
        return Result.success().msg("unlock success");
    }
}
