package com.fulu.game.thirdparty.fenqile.service;

import com.fulu.game.thirdparty.fenqile.entity.CodeSessionResult;
import com.fulu.game.thirdparty.fenqile.entity.FenqileUserInfo;

public interface FenqileAuthService {


    /**
     * 获取access_token
     * @param code
     * @return
     */
    public CodeSessionResult accessToken(String code);

    /**
     * 获取用户信息
     * @param codeSessionResult
     * @return
     */
    public FenqileUserInfo getUserInfo(CodeSessionResult codeSessionResult);
    /**
     * 获取用户信息
     * @param openId
     * @param accessToken
     * @return
     */
    public FenqileUserInfo getUserInfo(String openId, String accessToken);
}
