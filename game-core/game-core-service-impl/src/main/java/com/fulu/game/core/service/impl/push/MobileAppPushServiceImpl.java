package com.fulu.game.core.service.impl.push;

import com.fulu.game.core.entity.vo.AppPushMsgVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MobileAppPushServiceImpl extends PushServiceImpl {



    public void pushMsg(AppPushMsgVO appPushMsgVO){
        if(appPushMsgVO.getSendAll()){
            pushMsg(appPushMsgVO.getTitle(),appPushMsgVO.getAlert(),appPushMsgVO.getExtras());
        }else {
            pushMsg(appPushMsgVO.getTitle(),appPushMsgVO.getAlert(),appPushMsgVO.getExtras(),appPushMsgVO.getUserIds());
        }

    }


}
