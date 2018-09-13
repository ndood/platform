package com.fulu.game.h5.service.impl.fenqile;

import cn.hutool.core.util.StrUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.enums.WechatTemplateIdEnum;
import com.fulu.game.common.enums.WechatTemplateMsgEnum;
import com.fulu.game.common.enums.WechatTemplateMsgTypeEnum;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.impl.push.MiniAppPushServiceImpl;
import com.fulu.game.play.service.impl.PlayMiniAppPushServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class H5PushServiceImpl extends MiniAppPushServiceImpl {

    @Autowired
    private PlayMiniAppPushServiceImpl playMiniAppPushService;
    @Autowired
    private UserService userService;



    @Override
    protected void push(int userId, WechatTemplateMsgEnum wechatTemplateMsgEnum, String... replaces) {
        User user =  userService.findById(userId);
        if(user.getOpenId()!=null){
            pushWechatTemplateMsg(PlatformEcoEnum.PLAY.getType(),userId, WechatTemplateIdEnum.PLAY_LEAVE_MSG,wechatTemplateMsgEnum.getPage().getPlayPagePath(),wechatTemplateMsgEnum.getContent(),replaces);
        }else{
            String content = StrUtil.format(wechatTemplateMsgEnum.getContent(), replaces);
            SMSUtil.sendLeaveInform(user.getMobile(), content, Constant.WEIXN_JUMP_URL);
        }
    }

    @Override
    protected void push(int userId, Order order, WechatTemplateMsgEnum wechatTemplateMsgEnum, WechatTemplateMsgTypeEnum wechatTemplateMsgTypeEnum, String... replaces) {

    }


}
