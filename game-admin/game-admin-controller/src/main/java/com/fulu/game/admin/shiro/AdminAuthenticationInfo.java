package com.fulu.game.admin.shiro;

import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.util.ByteSource;


public class AdminAuthenticationInfo extends SimpleAuthenticationInfo {

    private long adminId;

    public AdminAuthenticationInfo(long adminId, Object principal, Object hashedCredentials, ByteSource credentialsSalt, String realmName) {
        super(principal, hashedCredentials, credentialsSalt, realmName);
        this.adminId = adminId;
    }

    private long getAdminId() {
        return adminId;
    }

}