package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.entity.vo.UserTechAuthVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController{

    @Autowired
    private UserInfoAuthService userInfoAuthService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserInfoFileService userInfoFileService;
    @Autowired
    private UserInfoAuthFileService userInfoAuthFileService;
    @Autowired
    private UserTechAuthService userTechAuthService;

    /**
     * 用户认证信息列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/info-auth/list")
    public Result userInfoAuthList(Integer pageNum,
                                   Integer pageSize){
        PageInfo<UserInfoAuthVO> pageInfo = userInfoAuthService.list(pageNum,pageSize,null);
        return Result.success().data(pageInfo);
    }


    /**
     * 认证信息创建
     * @return
     */
    @PostMapping(value = "/info-auth/save")
    public Result userInfoAuthCreate(UserInfoAuthVO userInfoAuthVO){
        userInfoAuthService.save(userInfoAuthVO);
        return Result.success().data(userInfoAuthVO);
    }

    /**
     * 删除身份证照片
     * @param id
     * @return
     */
    @PostMapping(value = "/info-auth/idcard/delete")
    public Result deleteIdCard(Integer id){
        userInfoFileService.deleteById(id);
        return Result.success().msg("删除成功!");
    }

    /**
     * 删除认证信息(写真和声音)
     * @param id
     * @return
     */
    @PostMapping(value = "/info-auth/file/delete")
    public Result deleteAuthFile(Integer id){
        userInfoAuthFileService.deleteById(id);
        return Result.success().msg("删除成功!");
    }


    /**
     * 查询用户个人认证信息
     * @param userId
     * @return
     */
    @PostMapping(value = "/info-auth/query")
    public Result userAuthInfo(@RequestParam(required=false,name = "userId") Integer userId){
        UserInfoAuthVO userInfoAuthVO =userInfoAuthService.findUserAuthInfoByUserId(userId);
        return Result.success().data(userInfoAuthVO);
    }

    /**
     * 通过用户手机查询用户信息
     * @param mobile
     * @return
     */
    @PostMapping(value = "/get")
    public Result findByMobile(String mobile){
        User user = userService.findByMobile(mobile);
        return Result.success().data(user);
    }

    /**
     * 用户技能认证信息添加和修改
     * @param userTechAuthVO
     * @return
     */
    @PostMapping(value = "/tech-auth/save")
    public Result techAuthSave(UserTechAuthVO userTechAuthVO){
        userTechAuthService.save(userTechAuthVO);
        return Result.success().data(userTechAuthVO);
    }

    /**
     * 用户技能认证信息查询
     * @return
     */
    @PostMapping(value = "/tech-auth/list")
    public Result techAuthList(Integer pageNum,
                               Integer pageSize){
        PageInfo<UserTechAuthVO> page= userTechAuthService.list(pageNum,pageSize,null);
        return Result.success().data(page);
    }

    /**
     * 用户技能认证信息查询
     * @param id
     * @return
     */
    @PostMapping(value = "/tech-auth/info")
    public Result techAuthInfo(Integer id){
        UserTechAuthVO userTechAuthVO = userTechAuthService.findTechAuthVOById(id);
        return Result.success().data(userTechAuthVO);
    }


}
