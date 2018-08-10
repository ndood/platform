package com.fulu.game.thirdparty.fenqile.service;

import com.fulu.game.thirdparty.fenqile.entity.CodeSessionResult;
import com.fulu.game.thirdparty.fenqile.entity.FenqileUserInfo;

public interface FenqileAuthService {


    public CodeSessionResult accessToken(String code);

    public FenqileUserInfo getUserInfo(CodeSessionResult codeSessionResult);

    public FenqileUserInfo getUserInfo(String openId, String accessToken);
}
