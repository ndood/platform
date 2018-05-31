package com.fulu.game.core.service;

import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserVO;
import com.github.pagehelper.PageInfo;
import me.chanjar.weixin.common.exception.WxErrorException;

import java.io.IOException;
import java.util.List;

/**
 * 用户表
 *
 * @author wangbin
 * @date 2018-04-20 11:12:12
 */
public interface UserService extends ICommonService<User, Integer> {

    User findByMobile(String mobile);

    List<User> findAllServeUser();

    User findByOpenId(String openId);

    User findByImId(String imId);

    List<User> findByImIds(String imIds);

    void lock(int id);

    void unlock(int id);

    PageInfo<User> list(UserVO userVO, Integer pageNum, Integer pageSize);

    User save(UserVO userVO);

    User getCurrentUser();

    void updateRedisUser(User user);

    Boolean isCurrentUser(Integer userId);

    String getTechAuthCard(Integer techAuthId, String scene, String page) throws WxErrorException, IOException;

    void checkUserInfoAuthStatus(Integer userId);

    String getTechShareCard(String scene,Integer productId) throws WxErrorException, IOException;
}
