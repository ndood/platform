package com.fulu.game.app.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.CategoryAuthStatusEnum;
import com.fulu.game.common.enums.CategoryParentEnum;
import com.fulu.game.common.enums.TechAuthStatusEnum;
import com.fulu.game.common.enums.UserInfoAuthStatusEnum;
import com.fulu.game.common.exception.UserAuthException;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.to.UserInfoAuthTO;
import com.fulu.game.core.entity.to.UserTechAuthTO;
import com.fulu.game.core.entity.vo.CategoryVO;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.entity.vo.UserTechAuthVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.UserTechAuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证相关接口
 */
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
    private CategoryService categoryService;



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
    public Result techAuthQuery(@RequestParam(name = "id", required = false) Integer id,
                                @RequestParam(name = "categoryId", required = false) Integer categoryId) {
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
        //验证用户的认证信息(暂改为只验证是否冻结)
//        userService.checkUserInfoAuthStatus(user.getId(), UserInfoAuthStatusEnum.ALREADY_PERFECT.getType());
        if (user.getUserInfoAuth().equals(UserInfoAuthStatusEnum.FREEZE.getType())) {
            throw new UserAuthException(UserAuthException.ExceptionCode.SERVICE_USER_FREEZE);
        }
        if (userTechAuthTO.getId() != null) {
            UserTechAuth userTechAuth = userTechAuthService.findById(userTechAuthTO.getId());
            userService.isCurrentUser(userTechAuth.getUserId());
        }
        userTechAuthTO.setUserId(user.getId());
        userTechAuthService.save(userTechAuthTO);
        return Result.success().data(userTechAuthTO);
    }


    /**
     * 申请资质认证页
     *
     * @return
     */
    @PostMapping(value = "/apply-auth-home")
    public Result userAuthHome() {
        // 1、获取用户认证状态；
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
        // 2、获取用户已认证技能列表
        List<UserTechAuth> techAuthList = userTechAuthService.findByUserId(user.getId());
        List<UserTechAuthEntity> userTechAuthVOList = new ArrayList<>();
        Map<String, UserTechAuthVO> authTechMap = new HashMap<>();
        for (UserTechAuth userTechAuth : techAuthList) {
            UserTechAuthVO userTechAuthVO = new UserTechAuthVO();
            BeanUtil.copyProperties(userTechAuth, userTechAuthVO);
            UserTechAuthReject techAuthReject = userTechAuthRejectService.findLastRecordByTechAuth(userTechAuth.getId(), userTechAuth.getStatus());
            if (techAuthReject != null) {
                userTechAuthVO.setReason(techAuthReject.getReason());
            }
            //认证通过的才放入集合中
            if(userTechAuthVO.getStatus() != null && userTechAuthVO.getStatus().intValue() == TechAuthStatusEnum.NORMAL.getType()){
                UserTechAuthEntity userTechAuthEntity = UserTechAuthEntity.builder().
                        techAuthId(userTechAuthVO.getId()).
                        categoryId(userTechAuthVO.getCategoryId()).
                        name(userTechAuthVO.getCategoryName()).status(userTechAuthVO.getStatus()).
                        build();
                userTechAuthVOList.add(userTechAuthEntity);
            }
            authTechMap.put(userTechAuthVO.getCategoryId() + "",userTechAuthVO);
        }
        map.put("userTechAuthList",userTechAuthVOList);
        JSONArray categoryGroupList = new JSONArray();
        // 3、获取用户可认证列表
        List<Category> list = categoryService.findByPid(CategoryParentEnum.ACCOMPANY_PLAY.getType(),true);
        if(list != null && list.size() > 0){
            for(Category category: list){
                CategoryVO categoryVO = new CategoryVO();
                List<UserTechAuthEntity> childCategoryList = new ArrayList<>();
                BeanUtil.copyProperties(category, categoryVO);
                List<Category> childList = categoryService.findByPid(category.getId(),true);
                String key = "";
                for(Category childCategory: childList){
                    CategoryVO childCategoryVO = new CategoryVO();
                    BeanUtil.copyProperties(childCategory, childCategoryVO);
                    key = childCategory.getId() + "";
                    Integer status = 0;
                    String statusStr = "";
                    Integer techAuthId = null;
                    String authRefuseReason = "";
                    if(authTechMap.containsKey(key) && authTechMap.get(key) != null &&
                            authTechMap.get(key).getStatus().intValue() == TechAuthStatusEnum.NORMAL.getType()){ //已认证
                        continue;
                    } else if(authTechMap.containsKey(key) && authTechMap.get(key) != null &&
                            authTechMap.get(key).getStatus().intValue() == TechAuthStatusEnum.AUTHENTICATION_ING.getType()){//审核中
                        childCategoryVO.setAuthStatus(CategoryAuthStatusEnum.AUTHING.getType());
                        childCategoryVO.setAuthStatusStr(CategoryAuthStatusEnum.AUTHING.getMsg());
                        status = CategoryAuthStatusEnum.AUTHING.getType();
                        statusStr = CategoryAuthStatusEnum.AUTHING.getMsg();
                        techAuthId = authTechMap.get(key).getId();
                    } else if(authTechMap.containsKey(key) && authTechMap.get(key) != null &&
                            authTechMap.get(key).getStatus().intValue() == TechAuthStatusEnum.NO_AUTHENTICATION.getType() ){//被拒绝
                        childCategoryVO.setAuthStatus(CategoryAuthStatusEnum.REFUSED.getType());
                        status = CategoryAuthStatusEnum.REFUSED.getType();
                        statusStr = CategoryAuthStatusEnum.REFUSED.getMsg();
                        techAuthId = authTechMap.get(key).getId();
                        childCategoryVO.setAuthStatusStr(CategoryAuthStatusEnum.REFUSED.getMsg());
                        childCategoryVO.setReason(authTechMap.get(key).getReason());
                        authRefuseReason = authTechMap.get(key).getReason();
                    } else {
                        childCategoryVO.setAuthStatus(CategoryAuthStatusEnum.UNAUTH.getType());
                        childCategoryVO.setAuthStatusStr(CategoryAuthStatusEnum.UNAUTH.getMsg());
                        status = CategoryAuthStatusEnum.UNAUTH.getType();
                        statusStr = CategoryAuthStatusEnum.UNAUTH.getMsg();
                    }
                    UserTechAuthEntity userTechAuthEntity = UserTechAuthEntity.builder().
                            techAuthId(techAuthId).
                            categoryId(childCategory.getId()).
                            name(childCategory.getName()).
                            status(status).
                            statusStr(statusStr).
                            reason(authRefuseReason).
                            build();
                    childCategoryList.add(userTechAuthEntity);
                }
                // 如果子分类存在，才需添加
                if(childCategoryList != null && childCategoryList.size() > 0){
                    categoryVO.setChildCategoryList(childCategoryList);
                    categoryGroupList.add(categoryVO);
                }
            }
        }
        map.put("techGroupList",categoryGroupList);
        return Result.success().data(map);
    }

}
