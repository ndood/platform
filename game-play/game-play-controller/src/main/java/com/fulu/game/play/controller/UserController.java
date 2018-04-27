package com.fulu.game.play.controller;

import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserTechAuth;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.UserTechAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController{

    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private UserService userService;

    @RequestMapping("tech/list")
    public Result userTechList(){
        //查询所有用户认证的技能
        List<UserTechAuth> techAuthList = userTechAuthService.findByUserId(Constant.DEF_USER_ID,true);
        return Result.success().data(techAuthList);
    }



    /**
     * 用户-注册
     * @param userVO
     * @return
     */
    @RequestMapping("/save")
    public Result save(@ModelAttribute UserVO userVO){
        User user = userService.save(userVO);
        return Result.success().data(user).msg("恭喜您注册成功！");
    }

    /**
     * 用户-查询余额
     * @param id
     * @return
     */
    @RequestMapping("/balance/get")
    public Result getBalance(@RequestParam("id") Integer id){
        User user = userService.findById(id);
        return Result.success().data(user.getBalance()).msg("查询成功！");
    }


}
