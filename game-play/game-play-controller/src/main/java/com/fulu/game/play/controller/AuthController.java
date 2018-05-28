package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.UserInfoAuthStatusEnum;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.UserInfoAuthReject;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController extends BaseController{

    @Autowired
    private UserInfoAuthService userInfoAuthService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserInfoAuthRejectService userInfoAuthRejectService;
    @Autowired
    private UserTechAuthService userTechAuthService;


    /**
     * 保存认证信息
     * @param userInfoAuthVO
     * @return
     */
    @PostMapping(value = "/user-info/save")
    public Result userAuthSave(UserInfoAuthVO userInfoAuthVO){
        //todo 判断用户是否是冻结
        userService.isCurrentUser(userInfoAuthVO.getUserId());
        userInfoAuthVO.setUserId(userService.getCurrentUser().getId());
        userInfoAuthService.save(userInfoAuthVO);
        return Result.success().data(userInfoAuthVO);
    }


    /**
     * 认证信息查询
     * @return
     */
    @PostMapping(value = "/user-info/query")
    public Result userAuthQuery(){
        User user = userService.getCurrentUser();
        UserInfoAuthVO userInfoAuthVO =userInfoAuthService.findUserAuthInfoByUserId(user.getId());
        return Result.success().data(userInfoAuthVO);
    }


    /**
     * 查询用户个信息认证状态
     * @return
     */
    @PostMapping(value = "/user-info/status")
    public Result userAuthStauts(){
        User user =userService.findById(userService.getCurrentUser().getId());
        Integer authStatus = user.getUserInfoAuth();
        UserInfoAuth userInfoAuth =userInfoAuthService.findByUserId(user.getId());
        if(userInfoAuth==null){
            authStatus = -1;
        }
        String reason = "";
        UserInfoAuthReject userInfoAuthReject = userInfoAuthRejectService.findLastRecordByUserId(user.getId(),authStatus);
        if(userInfoAuthReject!=null){
            reason = userInfoAuthReject.getReason();
        }
        Map<String,Object> map =new HashMap<>();
        map.put("authStatus",authStatus);
        map.put("reason",reason);
        return Result.success().data(reason);
    }

    /**
     * 查询用户所有认证技能
     * @return
     */
    @PostMapping(value = "/tech-info/query")
    public Result techAuthQuery(){
        User user = userService.getCurrentUser();
        List<UserTechAuth> techAuthList = userTechAuthService.findByUserId(user.getId());

        return Result.success().data(techAuthList);
    }






}
