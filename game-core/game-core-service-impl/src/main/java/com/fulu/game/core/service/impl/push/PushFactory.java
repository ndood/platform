package com.fulu.game.core.service.impl.push;

import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.core.service.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 2018/10/9.
 */

@Service
public class PushFactory {

    @Autowired
    private PlayMiniAppPushServiceImpl playMiniAppPushService;
    @Autowired
    private PointMiniAppPushServiceImpl pointMiniAppPushService;
    @Autowired
    private AppPushServiceImpl appPushService;
    @Autowired
    private SMSPushServiceImpl h5PushService;

    private static List<PushServiceImpl> pushServices = new ArrayList<>();

    @PostConstruct
    private void init() {
        pushServices.add(playMiniAppPushService);
        pushServices.add(pointMiniAppPushService);
        pushServices.add(appPushService);
        pushServices.add(h5PushService);

    }

    public PushServiceImpl create(Integer platform) {
        PlatformEcoEnum platformEcoEnum = PlatformEcoEnum.getEnumByType(platform);
        switch (platformEcoEnum) {
            case PLAY:
                return playMiniAppPushService;
            case POINT:
                return pointMiniAppPushService;
            case APP:
                return appPushService;
            default:
                return h5PushService;
        }
    }

    public List<PushServiceImpl> getPushIns() {
        return pushServices;
    }


}
