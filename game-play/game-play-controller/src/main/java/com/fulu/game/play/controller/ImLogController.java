package com.fulu.game.play.controller;


import com.fulu.game.common.Result;
import com.fulu.game.common.enums.VirtualProductTypeEnum;
import com.fulu.game.core.entity.AdminImLog;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserInfoAuthVO;
import com.fulu.game.core.entity.vo.UserOnlineVO;
import com.fulu.game.core.entity.vo.VirtualProductAttachVO;
import com.fulu.game.core.entity.vo.VirtualProductVO;
import com.fulu.game.core.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/imlog")
@Slf4j
public class ImLogController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private VirtualProductService virtualProductService;
    @Autowired
    private VirtualProductAttachService virtualProductAttachService;
    @Autowired
    private ImService imService;
    @Autowired
    private UserInfoAuthService userInfoAuthService;

    @PostMapping(value = "collect")
    public Result log(String content) {
        log.error("日志收集:{}", content);
        return Result.success();
    }


    @PostMapping(value = "online")
    public Result userOnline(@RequestParam(required = true) Boolean active, String version) {

        UserOnlineVO uo = userService.userOnline(active, version);

        return Result.success().data(uo).msg("查询成功！");
    }


    //增加陪玩师未读消息数量
    @RequestMapping("/send")
    public Result sendMessage(@RequestParam(value = "targetImId", required = false) String targetImId) {

        imService.addUnreadCount(targetImId);

        return Result.success().msg("操作成功");

    }


    /**
     * 解锁图片  声音  私照
     *
     * @return
     */
    @PostMapping(value = "/unlock")
    public Result unlockPrivatePic(Integer virtualProductId) {
        User user = userService.getCurrentUser();
        VirtualProductVO vpo = new VirtualProductVO();
        vpo.setUserId(user.getId());
        vpo.setType(VirtualProductTypeEnum.PERSONAL_PICS.getType());
        vpo.setDelFlag(false);

        virtualProductService.unlockProduct(user.getId(), virtualProductId);

        return Result.success().msg("解锁成功");

    }


    /**
     * 查看解锁商品
     *
     * @return
     */
    @PostMapping(value = "/unlock-product/list")
    public Result unlockProductList(Integer virtualProductId) {

        User user = userService.getCurrentUser();

        List<VirtualProductAttachVO> list = virtualProductAttachService.findByOrderProIdUserId(user.getId(), virtualProductId);

        return Result.success().data(list).msg("查询成功");
    }

    /**
     *
     *随机取一个自动问好的陪玩师
     * @return
     */
    @PostMapping(value = "/rand-auth/query")
    public Result randGetAuthUser() {
        UserInfoAuthVO userInfoAuthVO = userInfoAuthService.randGetAuthUser();
        return Result.success().data(userInfoAuthVO);
    }

}
