package com.fulu.game.app.controller;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.Result;
import com.fulu.game.common.domain.ClientInfo;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private OssUtil ossUtil;


    /**
     * 修改/填写资料
     * @param userVO
     * @return
     */
    @RequestMapping("update")
    public Result update(UserVO userVO){
        User user = userService.findById(userService.getCurrentUser().getId());
        user.setAge(DateUtil.ageOfNow(userVO.getBirth()));
        user.setGender(userVO.getGender());
        user.setCity(userVO.getCity());
        user.setProvince(userVO.getProvince());
        user.setCountry(userVO.getCountry());
        user.setBirth(userVO.getBirth());
        user.setConstellation(userVO.getConstellation());
        user.setNickname(userVO.getNickname());
        user.setHeadPortraitsUrl(ossUtil.activateOssFile(userVO.getHeadPortraitsUrl()));
        userService.update(user);
        userService.updateRedisUser(user);
        user.setIdcard(null);
        user.setRealname(null);
        return Result.success().data(user).msg("个人信息设置成功！");
    }


}
