package com.fulu.game.core.service.impl.push;

import com.fulu.game.common.enums.PlatformEcoEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private H5PushServiceImpl h5PushService;
    @Autowired
    private AllPushServiceImpl allPushService;

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

    public PushServiceImpl create() {
        return allPushService;
    }


}
