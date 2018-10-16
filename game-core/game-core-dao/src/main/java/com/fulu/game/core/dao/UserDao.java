package com.fulu.game.core.dao;

import com.fulu.game.core.entity.ImUser;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.entity.vo.searchVO.UserInfoAuthSearchVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户表
 *
 * @author wangbin
 * @email ${email}
 * @date 2018-04-20 11:12:12
 */
@Mapper
public interface UserDao extends ICommonDao<User, Integer> {

    List<User> findByParameter(UserVO userVO);

    int countByParameter(UserVO userVO);

    /**
     * 批量查询用户
     *
     * @param userIds
     * @return
     */
    List<User> findByUserIds(@Param(value = "userIds") List<Integer> userIds, @Param(value = "disabled") Boolean disabled);

    List<ImUser> findImNullUser();

    /**
     * 通过userIds查询imId
     * @param userIds
     * @return
     */
    List<String> findImIdsByUserIds(@Param(value = "userIds") List<Integer> userIds);

    /**
     * 查询userVo,vo包含用户推送间隔字段
     *
     * @param userIds
     * @return
     */
    List<UserVO> findUserVOByUserIds(@Param(value = "userIds") List<Integer> userIds);

    List<UserVO> findBySearch(UserVO userVO);

    List<UserVO> findServiceUserBySearch(UserInfoAuthSearchVO userInfoAuthSearchVO);

    List<User> findByExportParam(UserVO userVO);

    /**
     * 根据userId获取用户积分
     */
    Integer findUserScoreByUpdate(Integer userId);

    /**
     * 用户下单数量
     *
     * @return
     */
    Integer countUserOrder(Integer userId);


    List<User> searchByAuthUserInfo(@Param(value = "searchWord") String searchWord, @Param(value = "currentAdminId") Integer currentAdminId);


    List<User> searchByUserInfo(@Param(value = "searchWord") String searchWord, @Param(value = "currentAuthUserId") Integer currentAuthUserId);
}
