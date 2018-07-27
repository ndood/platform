package com.fulu.game.point.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.UserContact;
import com.fulu.game.core.entity.vo.UserContactVO;
import com.fulu.game.core.service.UserContactService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户联系方式Controller
 *
 * @author Gong ZeChun
 * @date 2018/7/27 14:20
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserContactController extends BaseController {
    private final UserContactService userContactService;

    @Autowired
    public UserContactController(UserContactService userContactService) {
        this.userContactService = userContactService;
    }

    /**
     * 保存陪玩师联系方式
     *
     * @param userContactVO 参数VO
     * @return 封装结果集
     */
    @PostMapping("/contact/save")
    public Result saveUserContact(UserContactVO userContactVO) {
        UserContact userContact = userContactService.saveUserContact(userContactVO);
        if (userContact == null) {
            return Result.error().msg("保存失败，参数为空或者手机号非法！");
        }
        return Result.success().data(userContact).msg("保存成功！");
    }


}
