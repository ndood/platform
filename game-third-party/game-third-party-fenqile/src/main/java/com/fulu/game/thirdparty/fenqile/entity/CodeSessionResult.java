package com.fulu.game.thirdparty.fenqile.entity;

import lombok.Data;

@Data
public class CodeSessionResult {

    private String accessToken;

    private String expiresIn;

    private String refreshToken;

    private String uid;


}
