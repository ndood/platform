package com.fulu.game.core.service;

import com.fulu.game.core.entity.ImUser;
import com.fulu.game.core.entity.vo.ImUserVo;

import java.util.List;

public interface ImService {

    /**
     * 调用环信接口的token
     *
     * @return
     */
    String getToken();

    /**
     * 批量获取IMUser
     *
     * @return
     */
    ImUserVo getUsers(Integer limit, String cursor);

    /**
     * 批量注册im用户
     * @param users
     */
    List<ImUser> registUsers(List<ImUser> users);



    ImUser registerUser(String imId, String imPsw);
}
