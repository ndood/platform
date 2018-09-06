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
     *
     * @param users
     */
    List<ImUser> registUsers(List<ImUser> users);


    ImUser registerUser(String imId, String imPsw);

    /**
     * 发送陪玩师已接单的IM信息到用户
     *
     * @param imId   环信id
     * @param action
     * @return 是否发送成功
     */
    boolean sendMsgToImUser(String imId, String action);


    /**
     * 增加陪玩师未读消息
     * @param targetImId
     */
    void addUnreadCount(String targetImId);
}
