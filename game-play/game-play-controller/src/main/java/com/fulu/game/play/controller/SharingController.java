package com.fulu.game.play.controller;

import cn.hutool.core.bean.BeanUtil;
import com.fulu.game.common.Result;
import com.fulu.game.core.entity.Sharing;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.SharingVO;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.service.SharingService;
import com.fulu.game.core.service.UserInfoAuthService;
import com.fulu.game.core.service.UserService;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/sharing")
public class SharingController extends BaseController {

    @Autowired
    private SharingService sharingService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserInfoAuthService userInfoAuthService;

    @PostMapping("/get")
    public Result get(@RequestParam("shareType") Integer shareType) {
        User user = userService.getCurrentUser();
        Integer gender = 1;//除女用户外文案均为男性别文案
        if (null != user.getGender() && 2 == user.getGender()) {
            gender = 2;
        }
        SharingVO queryVO = new SharingVO();
        queryVO.setShareType(shareType);
        queryVO.setGender(gender);
        queryVO.setStatus(true);
        List<Sharing> sharingList = sharingService.findByParam(queryVO);
        if (!CollectionUtil.isEmpty(sharingList)) {
            SharingVO sharingVO = new SharingVO();
            BeanUtil.copyProperties(sharingList.get(0), sharingVO);
            UserInfoAuthVO userInfoAuthVO = userInfoAuthService.findUserAuthInfoByUserId(user.getId());
            if (null != userInfoAuthVO) {
                sharingVO.setMainPicUrl(userInfoAuthVO.getMainPicUrl());
            }
            return Result.success().data(sharingVO).msg("查询成功！");
        } else {
            return Result.error().msg("尚未配置或文案弃用！");
        }

    }
}
