package com.fulu.game.admin.config;

import com.fulu.game.common.domain.Password;
import com.fulu.game.common.utils.EncryptUtil;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.InitializingBean;

/**
 * 用于验证密码是否匹配
 * @author LiuPiao
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher implements InitializingBean {



	@Override
	public void afterPropertiesSet() throws Exception {
	}


    /**
     * 验证密码是否匹配逻辑
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
    	boolean isMatch = match(token,info);
        return isMatch;
    }
    
    /**
     * 自定义验证逻辑
     * @param token
     * @param info
     * @return
     */
    private boolean match(AuthenticationToken token, AuthenticationInfo info){
    	String salt = "";
        if(info instanceof SaltedAuthenticationInfo){
        	ByteSource bs = ((SaltedAuthenticationInfo)info).getCredentialsSalt();
            try {
                salt = new String(bs.getBytes(),"utf-8");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        Password passObj = EncryptUtil.PiecesEncode(new String((char[])token.getCredentials()), salt);
        String tokenHashedCredentials = passObj.getPassword();
        String infoHashCredentials = (String)info.getCredentials();
        return super.equals(tokenHashedCredentials, infoHashCredentials);
    }


}
