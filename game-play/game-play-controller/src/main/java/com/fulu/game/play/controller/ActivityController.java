package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.OfficialActivityVO;
import com.fulu.game.core.service.OfficialActivityService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.play.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("/api/v1/activity")
public class ActivityController extends BaseController{

    @Autowired
    private OfficialActivityService officialActivityService;
    @Autowired
    private UserService userService;

    /**
     * 获取app活动
     * @return
     */
    @RequestMapping(value = "get")
    public Result get(){
        OfficialActivityVO officialActivityVO = officialActivityService.getUsableActivity(PlatformEcoEnum.APP);
        return Result.success().data(officialActivityVO);
    }


    @RequestMapping(value = "join")
    public Result join(Integer activityId,
                       HttpServletRequest request){
        User user = userService.getCurrentUser();
        String ip = RequestUtil.getIpAdrress(request);
        Boolean flag = officialActivityService.userJoinActivity(user.getId(),activityId,ip);
        if(flag){
            return Result.success().data("参与活动成功!");
        }
        return Result.error().data("已经参与过一次活动了!");
    }


}
