package com.fulu.game.core.service.impl.push;

import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import com.fulu.game.common.enums.WechatEcoEnum;
import com.fulu.game.common.enums.WechatTemplateEnum;
import com.fulu.game.core.entity.WxMaTemplateMessageVO;
import com.fulu.game.core.entity.vo.WechatFormidVO;
import com.fulu.game.core.service.WechatFormidService;
import com.fulu.game.core.service.queue.PushMsgQueue;
import com.xiaoleilu.hutool.date.DateField;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AdminPushServiceImpl extends PushServiceImpl {

    @Autowired
    private PlayMiniAppPushServiceImpl playMiniAppPushService;
    @Autowired
    private PointMiniAppPushServiceImpl pointMiniAppPushService;

    /**
     * 管理员推送自定义通知
     *
     * @param pushId
     * @param userIds
     * @param page
     * @param content
     */
    public void adminPushWxTemplateMsg(int platform, int pushId, List<Integer> userIds, String page, String content) {
        String date = DateUtil.format(new Date(), "yyyy年MM月dd日 HH:mm");
        List<WxMaTemplateMessage.Data> dataList = CollectionUtil.newArrayList(new WxMaTemplateMessage.Data("keyword1", content),
                new WxMaTemplateMessage.Data("keyword2", date));
        if (WechatEcoEnum.POINT.getType().equals(platform)) {
            pointMiniAppPushService.addTemplateMsg2Queue(platform, pushId, userIds, page, WechatTemplateEnum.POINT_LEAVE_MSG, dataList);
        } else {
            playMiniAppPushService.addTemplateMsg2Queue(platform, pushId, userIds, page, WechatTemplateEnum.PLAY_LEAVE_MSG, dataList);
        }
    }


}
