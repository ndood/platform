package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.common.threadpool.SpringThreadPoolExecutor;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.WechatFormid;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.WechatFormidService;
import com.fulu.game.core.service.WxTemplateMsgService;
import com.fulu.game.play.queue.CollectFormIdQueue;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/api/v1/wxtemplate")
public class WxTemplateController {

    @Autowired
    private WxTemplateMsgService wxTemplateMsgService;
    @Autowired
    private UserService userService;
    @Autowired
    private CollectFormIdQueue collectFormIdQueue;

    /**
     * 收集用户的formID
     * @param formId
     * @return
     */
    @RequestMapping("/collect")
    public Result collectFormId(String formId){
        User user =userService.getCurrentUser();
        WechatFormid wechatFormid = new WechatFormid();
        wechatFormid.setUserId(user.getId());
        wechatFormid.setFormId(formId);
        wechatFormid.setCreateTime(new Date());
        collectFormIdQueue.addFormId(wechatFormid);
        log.info("收集formId成功:formId:{}",formId);
        return Result.success();
    }


    @RequestMapping("/push")
    public Result pushWechatMsg(String content,
                                String acceptImId,
                                String imId)throws Exception{
        log.info("推送模板消息imId:{},acceptImId:{},content:{}",imId,acceptImId,content);
        String result = wxTemplateMsgService.pushIMWxTemplateMsg(content,acceptImId,imId);
        return Result.success().msg(result);
    }






}
