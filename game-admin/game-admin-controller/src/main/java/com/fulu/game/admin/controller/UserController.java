package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.ResultStatus;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.entity.vo.UserTechAuthVO;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {

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
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/info-auth/list")
    public Result userInfoAuthList(@RequestParam("pageNum") Integer pageNum,
                                   @RequestParam("pageSize") Integer pageSize,
                                   @RequestParam(value = "startTime", required = false) String startTime,
                                   @RequestParam(value = "endTime", required = false) String endTime,
                                   @RequestParam(value = "mobile", required = false) String mobile) {
        String orderBy = null;
        PageInfo<UserInfoAuthVO> pageInfo = userInfoAuthService.list(pageNum, pageSize, orderBy, mobile, startTime, endTime);
        return Result.success().data(pageInfo);
    }


    /**
     * 认证信息创建
     *
     * @return
     */
    @PostMapping(value = "/info-auth/save")
    public Result userInfoAuthCreate(UserInfoAuthVO userInfoAuthVO) {
        userInfoAuthService.save(userInfoAuthVO);
        return Result.success().data(userInfoAuthVO);
    }


    /**
     * 认证信息驳回
     * @param id
     * @return
     */
    @PostMapping(value = "/info-auth/reject")
    public Result userInfoAuthReject(Integer id,
                                     String reason){


        return Result.success();
    }



    /**
     * 删除身份证照片
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/info-auth/idcard/delete")
    public Result deleteIdCard(@RequestParam(required = true) Integer id) {
        userInfoFileService.deleteById(id);
        return Result.success().msg("删除成功!");
    }

    /**
     * 删除认证信息(写真和声音)
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/info-auth/file/delete")
    public Result deleteAuthFile(@RequestParam(required = true) Integer id) {
        userInfoAuthFileService.deleteById(id);
        return Result.success().msg("删除成功!");
    }


    /**
     * 查询用户个人认证信息
     *
     * @param userId
     * @return
     */
    @PostMapping(value = "/info-auth/query")
    public Result userAuthInfo(@RequestParam(required = false, name = "userId") Integer userId) {
        UserInfoAuthVO userInfoAuthVO = userInfoAuthService.findUserAuthInfoByUserId(userId);
        return Result.success().data(userInfoAuthVO);
    }

    /**
     * 通过用户手机查询用户信息
     *
     * @param mobile
     * @return
     */
    @PostMapping(value = "/get")
    public Result findByMobile(@RequestParam(required = true) String mobile) {
        User user = userService.findByMobile(mobile);
        if (user == null) {
            return Result.error().msg("手机号查询错误!");
        }
        return Result.success().data(user);
    }

    /**
     * 用户技能认证信息添加和修改
     * @param userTechAuthVO
     * @return
     */
    @PostMapping(value = "/tech-auth/save")
    public Result techAuthSave(UserTechAuthVO userTechAuthVO) {
        userTechAuthService.save(userTechAuthVO);
        return Result.success().data(userTechAuthVO);
    }

    /**
     * 用户技能认证信息查询
     * @return
     */
    @PostMapping(value = "/tech-auth/list")
    public Result techAuthList(@RequestParam("pageNum") Integer pageNum,
                               @RequestParam("pageSize") Integer pageSize,
                               @RequestParam(value = "nickname", required = false) String nickname,
                               @RequestParam(value = "mobile", required = false) String mobile,
                               @RequestParam(value = "startTime", required = false) String startTime,
                               @RequestParam(value = "endTime", required = false) String endTime) {
        UserTechAuthVO requestVo = new UserTechAuthVO();
        requestVo.setNickname(nickname);
        requestVo.setStartTime(startTime);
        requestVo.setEndTime(endTime);
        requestVo.setMobile(mobile);
        PageInfo<UserTechAuthVO> page = userTechAuthService.list(pageNum, pageSize, null, requestVo);
        return Result.success().data(page);
    }

    /**
     * 用户技能认证信息查询
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/tech-auth/query")
    public Result techAuthInfo(@RequestParam(required = true) Integer id) {
        UserTechAuthVO userTechAuthVO = userTechAuthService.findTechAuthVOById(id);
        return Result.success().data(userTechAuthVO);
    }

    /**
     * 单个用户-封禁
     *
     * @param id
     * @return
     */
    @PostMapping("/lock")
    public Result lock(@RequestParam("id") Integer id) {
        userService.lock(id);
        log.info("用户id {} 于 {} 被封禁", id, new Date());
        return Result.success().msg("操作成功！");
    }

    /**
     * 单个用户-解封
     *
     * @param id
     * @return
     */
    @PostMapping("/unlock")
    public Result unlock(@RequestParam("id") Integer id) {
        userService.unlock(id);
        log.info("用户id {} 于 {} 被解封", id, new Date());
        return Result.success().msg("操作成功！");
    }

    /**
     * 查询-用户-列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping("/list")
    public Result list(UserVO userVO,
                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageInfo<User> userList = userService.list(userVO, pageNum, pageSize);
        return Result.success().data(userList).msg("查询用户列表成功！");
    }

    @PostMapping("/save")
    public Result save(@Valid UserVO userVO) {
        if (StringUtils.isEmpty(userVO.getMobile())) {
            throw new UserException(UserException.ExceptionCode.IllEGAL_MOBILE_EXCEPTION);
        }
        //判断手机号是否已注册成用户
        User user = userService.findByMobile(userVO.getMobile());
        if (user != null) {
            return Result.error(ResultStatus.MOBILE_DUPLICATE).msg("手机号已注册");
        } else {
            User newUser = userService.save(userVO);
            return Result.success().data(newUser).msg("新用户添加成功！");
        }
    }

}
