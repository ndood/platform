package com.fulu.game.core.service;

import com.fulu.game.core.entity.ImUser;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserVO;
import com.github.pagehelper.PageInfo;
import me.chanjar.weixin.common.exception.WxErrorException;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 用户表
 *
 * @author wangbin
 * @date 2018-04-20 11:12:12
 */
public interface UserService extends ICommonService<User, Integer> {

    /**
     * 根据手机号查询user
     *
     * @param mobile
     * @return
     */
    User findByMobile(String mobile);

    /**
     * 查询所有陪玩师
     *
     * @return
     */
    List<User> findAllServeUser();

    /**
     * 根据用户类型和登录时间查询
     *
     * @return
     */
    List<User> findByLoginTime(Integer userType, Date startTime, Date endTime);

    /**
     * 根据openId查询user
     *
     * @param openId
     * @return
     */
    User findByOpenId(String openId);

    /**
     * 根据imId查询user
     *
     * @param imId
     * @return
     */
    User findByImId(String imId);

    /**
     * 根据imIds（逗号隔开）批量查询users
     *
     * @param imIds
     * @return
     */
    List<User> findByImIds(String imIds);

    /**
     * 查询所有状态正常的用户
     *
     * @return
     */
    List<User> findAllNormalUser();

    /**
     * 查询所有正常用户的数量
     *
     * @return
     */
    Integer countAllNormalUser();

    /**
     * 通过UserId查询一批用户
     *
     * @param userIds
     * @return
     */
    List<User> findByUserIds(List<Integer> userIds);


    void updateRedisUser(User user);

    /**
     * 后台封禁用户（仅修改user表用户的状态）
     *
     * @param id
     */
    void lock(int id);

    /**
     * 后台解封用户(仅修改user表用户的状态)
     *
     * @param id
     */
    void unlock(int id);

    /**
     * 后台（条件）查询用户列表
     *
     * @param userVO
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<UserVO> list(UserVO userVO, Integer pageNum, Integer pageSize);

    /**
     * 新增用户
     *
     * @param userVO
     * @return
     */
    User createNewUser(UserVO userVO);

    /**
     * 通过openId创建用户
     *
     * @param openId
     * @return
     */
    User createNewUser(String openId, Integer sourceId, String host);

    /**
     * 获取当前登录用户
     *
     * @return
     */
    User getCurrentUser();

    /**
     * 更新用户最后登录时间和IP
     * @param ip
     * @return
     */
    User updateUserIpAndLastTime(String ip);


    /**
     * 判断是否是当前用户
     *
     * @param userId
     * @return
     */
    Boolean isCurrentUser(Integer userId);

    /**
     * 生成技能认证图片粉分享url
     *
     * @param techAuthId
     * @param scene
     * @return
     * @throws WxErrorException
     * @throws IOException
     */
    String getTechAuthCard(Integer techAuthId, String scene) throws WxErrorException, IOException;

    /**
     * 生成陪玩师技能分享图片url
     *
     * @param scene
     * @param productId
     * @return
     * @throws WxErrorException
     * @throws IOException
     */
    String getTechShareCard(String scene, Integer productId) throws WxErrorException, IOException;

    /**
     * 查询陪玩师是否是可用状态
     *
     * @param userId
     */
    void checkUserInfoAuthStatus(Integer userId,Integer ... ignoreAuthStatus);

    /**
     * 查询imid为空的用户
     *
     * @return
     */
    List<ImUser> findImNullUser();

    /**
     * 绑定im信息
     *
     * @param imUser
     */
    void bindIm(ImUser imUser);





    List<UserVO> findVOByUserIds(List<Integer> userIds);
}
