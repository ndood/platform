package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.WechatFormid;
import com.fulu.game.core.service.PushMsgService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.push.PlayMiniAppPushServiceImpl;
import com.fulu.game.core.service.queue.FormIdContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/api/v1/wxtemplate")
public class WxTemplateController extends BaseController {


    @Autowired
    private UserService userService;
    @Autowired
    private FormIdContainer formIdContainer;
    @Autowired
    private PushMsgService pushMsgService;
    @Autowired
    private PlayMiniAppPushServiceImpl playMiniAppPushService;

    /**
     * 收集用户的formID
     *
     * @param formId
     * @return
     */
    @RequestMapping("/collect")
    public Result collectFormId(String formId) {
        User user = userService.getCurrentUser();
        WechatFormid wechatFormid = new WechatFormid();
        wechatFormid.setUserId(user.getId());
        wechatFormid.setFormId(formId);
        wechatFormid.setOpenId(user.getOpenId());
        wechatFormid.setPlatform(PlatformEcoEnum.PLAY.getType());
        wechatFormid.setCreateTime(new Date());
        if (wechatFormid.getOpenId() == null) {
            log.error("收集formId的时候没有改用户没有openId,user:{}", user);
            User dbUser = userService.findById(user.getId());
            wechatFormid.setOpenId(dbUser.getOpenId());
        }
        formIdContainer.add(wechatFormid);
        log.info("收集formId成功:formId:{}", formId);
        return Result.success();
    }


    @RequestMapping("/push")
    public Result pushWechatMsg(String content,
                                String acceptImId,
                                String imId) throws Exception {
        log.info("推送模板消息imId:{},acceptImId:{},content:{}", imId, acceptImId, content);
        String result = playMiniAppPushService.pushIMWxTemplateMsg(content, acceptImId, imId);
        return Result.success().msg(result);
    }


    @RequestMapping("/pushmsg/hits")
    public Result pushMsgHits(@RequestParam(required = true) Integer pushId) throws Exception {
        pushMsgService.hitsStatistics(pushId);
        return Result.success();
    }


}
