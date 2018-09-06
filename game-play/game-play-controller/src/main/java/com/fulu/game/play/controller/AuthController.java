package com.fulu.game.play.controller;

import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.UserInfoAuthStatusEnum;
import com.fulu.game.common.enums.VirtualProductTypeEnum;
import com.fulu.game.common.exception.UserAuthException;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.to.UserInfoAuthTO;
import com.fulu.game.core.entity.to.UserTechAuthTO;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.entity.vo.UserTechAuthVO;
import com.fulu.game.core.entity.vo.VirtualProductVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.UserTechAuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @Qualifier(value = "userTechAuthServiceImpl")
    @Autowired
    private UserTechAuthServiceImpl userTechAuthService;
    @Autowired
    private UserTechAuthRejectService userTechAuthRejectService;
    @Autowired
    private VirtualProductService virtualProductService;


    /**
     * 新保存陪玩师资料接口
     *
     * @param userInfoAuthTO
     * @return
     */
    @PostMapping(value = "/user-info/save")
    public Result save(UserInfoAuthTO userInfoAuthTO) {
        User user = userService.getCurrentUser();
        if (userInfoAuthTO.getId() != null) {
            UserInfoAuth userInfoAuth = userInfoAuthService.findById(userInfoAuthTO.getId());
            userService.isCurrentUser(userInfoAuth.getUserId());
        }
        userInfoAuthTO.setUserId(user.getId());
        UserInfoAuth userInfoAuth = userInfoAuthService.save(userInfoAuthTO);
        return Result.success().data(userInfoAuth.getId()).msg("个人资料保存成功!");
    }


    /**
     * 认证信息查询
     *
     * @return
     */
    @PostMapping(value = "/user-info/query")
    public Result userAuthQuery() {
        User user = userService.getCurrentUser();
        UserInfoAuthVO userInfoAuthVO = userInfoAuthService.findUserInfoAuthByUserId(user.getId());
        return Result.success().data(userInfoAuthVO);
    }


    /**
     * 查询用户个信息认证状态
     *
     * @return
     */
    @PostMapping(value = "/user-info/status")
    public Result userAuthStatus() {
        User user = userService.findById(userService.getCurrentUser().getId());
        //如果是用户冻结状态给错误提示
        if (user.getUserInfoAuth().equals(UserInfoAuthStatusEnum.FREEZE.getType())) {
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
     *
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
            UserTechAuthReject techAuthReject = userTechAuthRejectService.findLastRecordByTechAuth(userTechAuth.getId(), userTechAuth.getStatus());
            if (techAuthReject != null) {
                userTechAuthVO.setReason(techAuthReject.getReason());
            }
            userTechAuthVOList.add(userTechAuthVO);
        }
        return Result.success().data(userTechAuthVOList);
    }

    /**
     * 查询用户技能认证信息
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/tech-info/query")
    public Result techAuthQuery(Integer id,
                                Integer categoryId) {
        UserTechAuthVO userTechAuthVO = userTechAuthService.findTechAuthVOById(id, categoryId);
        if (userTechAuthVO.getUserId() != null) {
            userService.isCurrentUser(userTechAuthVO.getUserId());
        }
        return Result.success().data(userTechAuthVO);
    }


    /**
     * 保存用户技能认证信息
     *
     * @param userTechAuthTO
     * @return
     */
    @PostMapping(value = "/tech-info/save")
    public Result techAuthSave(UserTechAuthTO userTechAuthTO) {
        User user = userService.getCurrentUser();
        //验证用户的认证信息
        userService.checkUserInfoAuthStatus(user.getId(), UserInfoAuthStatusEnum.ALREADY_PERFECT.getType());
        if (userTechAuthTO.getId() != null) {
            UserTechAuth userTechAuth = userTechAuthService.findById(userTechAuthTO.getId());
            userService.isCurrentUser(userTechAuth.getUserId());
        }
        userTechAuthTO.setUserId(user.getId());
        userTechAuthService.save(userTechAuthTO);
        return Result.success().data(userTechAuthTO);
    }

    /**
     * 获取陪玩师私照列表
     * @return
     */
    @PostMapping(value = "/private-pic/list")
    public Result privatePicList(Integer userId) {

        User user = userService.getCurrentUser();
        
        VirtualProductVO vpo = new VirtualProductVO();
        vpo.setUserId(userId);
        vpo.setType(VirtualProductTypeEnum.PERSONAL_PICS.getType());
        vpo.setDelFlag(false);
        vpo.setTargetUserId(user.getId());

        List<VirtualProductVO> list = virtualProductService.searchByvirtualProductVo(vpo);

        return Result.success().data(list).msg("查询成功");
    }

}
