package com.fulu.game.play.controller;

import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.UserInfoAuthStatusEnum;
import com.fulu.game.common.exception.UserAuthException;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.entity.vo.UserTechAuthVO;
import com.fulu.game.core.service.*;
import com.xiaoleilu.hutool.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController extends BaseController {

    @Autowired
    private UserInfoAuthService userInfoAuthService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserInfoAuthRejectService userInfoAuthRejectService;
    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private UserTechAuthRejectService userTechAuthRejectService;

     /**
     * 保存认证信息
     * @param userInfoAuthVO
     * @return
     */
    @PostMapping(value = "/user-info/save")
    public Result userAuthSave(UserInfoAuthVO userInfoAuthVO) {
        User user = userService.getCurrentUser();
        if(userInfoAuthVO.getId()!=null){
            UserInfoAuth userInfoAuth = userInfoAuthService.findById(userInfoAuthVO.getId());
            userService.isCurrentUser(userInfoAuth.getUserId());
        }
        userInfoAuthVO.setUserId(user.getId());
        userInfoAuthService.save(userInfoAuthVO);
        return Result.success().data(userInfoAuthVO.getId()).msg("认证信息保存成功!");
    }


    /**
     * 认证信息查询
     * @return
     */
    @PostMapping(value = "/user-info/query")
    public Result userAuthQuery() {
        User user = userService.getCurrentUser();
        UserInfoAuthVO userInfoAuthVO = userInfoAuthService.findUserAuthInfoByUserId(user.getId());
        return Result.success().data(userInfoAuthVO);
    }


    /**
     * 查询用户个信息认证状态
     * @return
     */
    @PostMapping(value = "/user-info/status")
    public Result userAuthStatus() {
        User user = userService.findById(userService.getCurrentUser().getId());
        //如果是用户冻结状态给错误提示
        if(user.getUserInfoAuth().equals(UserInfoAuthStatusEnum.FREEZE.getType())){
            throw new UserAuthException(UserAuthException.ExceptionCode.SERVICE_USER_FREEZE);
        }
        Integer authStatus = user.getUserInfoAuth();
        UserInfoAuth userInfoAuth = userInfoAuthService.findByUserId(user.getId());
        if (userInfoAuth == null) {
            authStatus = -1;
        }
        String reason = "";
        UserInfoAuthReject userInfoAuthReject = userInfoAuthRejectService.findLastRecordByUserId(user.getId(), authStatus);
        if (userInfoAuthReject != null) {
            reason = userInfoAuthReject.getReason();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("authStatus", authStatus);
        map.put("reason", reason);
        return Result.success().data(map);
    }

    /**
     * 查询用户所有技能认证
     * @return
     */
    @PostMapping(value = "/tech-info/list")
    public Result techAuthList() {
        User user = userService.getCurrentUser();
        List<UserTechAuth> techAuthList = userTechAuthService.findByUserId(user.getId());
        List<UserTechAuthVO> userTechAuthVOList = new ArrayList<>();
        for (UserTechAuth userTechAuth : techAuthList) {
            UserTechAuthVO userTechAuthVO = new UserTechAuthVO();
            BeanUtil.copyProperties(userTechAuth, userTechAuthVO);
            userTechAuthVO.setApproveCountStr(userTechAuth.getApproveCount() + "/" + Constant.DEFAULT_APPROVE_COUNT);
            UserTechAuthReject techAuthReject =userTechAuthRejectService.findLastRecordByTechAuth(userTechAuth.getId(),userTechAuth.getStatus());
            if(techAuthReject!=null){
                userTechAuthVO.setReason(techAuthReject.getReason());
            }
            userTechAuthVOList.add(userTechAuthVO);
        }
        return Result.success().data(userTechAuthVOList);
    }

    /**
     * 查询用户技能认证信息
     * @param id
     * @return
     */
    @PostMapping(value = "/tech-info/query")
    public Result techAuthQuery(@RequestParam(required = true) Integer id) {
        UserTechAuthVO userTechAuthVO = userTechAuthService.findTechAuthVOById(id);
        userService.isCurrentUser(userTechAuthVO.getUserId());
        return Result.success().data(userTechAuthVO);
    }


    /**
     * 保存用户技能认证信息
     * @param userTechAuthVO
     * @return
     */
    @PostMapping(value = "/tech-info/save")
    public Result techAuthSave(UserTechAuthVO userTechAuthVO) {
        User user = userService.getCurrentUser();
        //验证用户的认证信息
        userService.checkUserInfoAuthStatus(user.getId());
        if(userTechAuthVO.getId()!=null){
            UserTechAuth userTechAuth = userTechAuthService.findById(userTechAuthVO.getId());
            userService.isCurrentUser(userTechAuth.getUserId());
        }
        userTechAuthVO.setUserId(user.getId());
        userTechAuthService.save(userTechAuthVO);
        return Result.success().data(userTechAuthVO);
    }




}
