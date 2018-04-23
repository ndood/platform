package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.UserTechAuthVO;
import com.fulu.game.core.service.UserTechAuthService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/tech")
public class TechAuthController extends BaseController{

    @Autowired
    private UserTechAuthService userTechAuthService;

    /**
     * 用户技能认证信息添加和修改
     * @param userTechAuthVO
     * @return
     */
    @PostMapping(value = "/auth/save")
    public Result techAuthSave(UserTechAuthVO userTechAuthVO){
        userTechAuthService.save(userTechAuthVO);
        return Result.success().data(userTechAuthVO);
    }

    /**
     * 用户技能认证信息查询
     * @return
     */
    @PostMapping(value = "/auth/list")
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
    @PostMapping(value = "/auth/info")
    public Result techAuthInfo(Integer id){
        UserTechAuthVO userTechAuthVO = userTechAuthService.findTechAuthVOById(id);
        return Result.success().data(userTechAuthVO);
    }

}
