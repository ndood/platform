package com.fulu.game.admin.shiro;

import com.fulu.game.common.domain.Password;
import com.fulu.game.common.utils.EncryptUtil;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.AdminVO;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * 用于验证密码是否匹配
 * @author LiuPiao
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher implements InitializingBean {

    @Autowired
    private RedisOpenServiceImpl redisOpenService;

	@Override
	public void afterPropertiesSet() throws Exception {
	}


    /**
     * 验证密码是否匹配逻辑
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
    	return match(token,info);
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
        if(super.equals(tokenHashedCredentials, infoHashCredentials)){
//            AdminVO adminVO = new AdminVO();
//            adminVO.setId(info.getA).setMobile();
//            redisOpenService.hset(UUID.randomUUID(), );
            return true;
        }
        return false;
    }


}
