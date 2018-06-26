package com.fulu.game.play.controller;


import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/v1/global")
@Slf4j
public class GlobalController extends BaseController{

    @Autowired
    private OssUtil ossUtil;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private UserService userService;



    @PostMapping(value = "upload")
    public Result upload(@RequestParam("file") MultipartFile file,String name)throws Exception{
        String fileName =  ossUtil.uploadFile(file.getInputStream(),file.getOriginalFilename());
        return Result.success().data(fileName).msg(name);
    }


    @PostMapping(value = "form/sessionkey")
    public Result formToken(){
        String sessionkey = GenIdUtil.GetToken();
        User user = userService.getCurrentUser();
        redisOpenService.set(RedisKeyEnum.GLOBAL_FORM_TOKEN.generateKey(sessionkey),user.getId()+"", Constant.TIME_HOUR_FIVE);
        return Result.success().data(sessionkey);
    }



}
