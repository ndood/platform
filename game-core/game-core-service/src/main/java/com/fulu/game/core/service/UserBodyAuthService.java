package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserBodyAuth;
import com.fulu.game.core.entity.vo.UserBodyAuthVO;
import com.github.pagehelper.PageInfo;

import java.util.List;


/**
 * 用户身份认证信息表
 *
 * @author jaycee.Deng
 * @email ${email}
 * @date 2018-09-05 17:40:37
 */
public interface UserBodyAuthService extends ICommonService<UserBodyAuth, Integer> {

    List<UserBodyAuth> findByParameter(UserBodyAuthVO userBodyAuthVO);


    void submitUserBodyAuthInfo(UserBodyAuthVO userBodyAuthVO);

    /**
     * 通过审核
     *
     * @param userId 用户id
     * @return 是否操作成功
     */
    boolean pass(Integer userId);

    /**
     * 拒绝或者驳回
     *
     * @param userId 用户id
     * @param remark 备注
     * @return 是否操作成功
     */
    boolean reject(Integer userId, String remark);


    PageInfo<UserBodyAuthVO> findByVO(Integer pageNum, Integer pageSize, UserBodyAuthVO userBodyAuthVO);

    List<UserBodyAuthVO> list(UserBodyAuthVO userBodyAuthVO);


    UserBodyAuth findByUserId(Integer userId);

    /**
     * 用户是否已认证
     * @param userId
     * @return
     */
    boolean userAlreadyAuth(Integer userId);


    UserBodyAuth getUserAuthInfo(Integer userId);

}
