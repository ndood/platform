package com.fulu.game.core.service.impl;

import cn.hutool.json.JSONObject;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.ImgUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserDao;
import com.fulu.game.core.entity.Sharing;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.SharingVO;
import com.fulu.game.core.entity.vo.UserInfoVO;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.service.SharingService;
import com.fulu.game.core.service.UserInfoAuthService;
import com.fulu.game.core.service.UserService;
import com.fulu.game.core.service.WxCodeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.util.BeanUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service("userService")
public class UserServiceImpl extends AbsCommonService<User, Integer> implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private UserInfoAuthService userInfoAuthService;
    @Autowired
    private SharingService sharingService;
    @Autowired
    private WxCodeService wxCodeService;
    @Autowired
    private ImgUtil imgUtil;

    @Override
    public ICommonDao<User, Integer> getDao() {
        return userDao;
    }

    @Override
    public User findByMobile(String mobile) {
        UserVO userVO = new UserVO();
        userVO.setMobile(mobile);
        List<User> users = userDao.findByParameter(userVO);
        if (CollectionUtil.isEmpty(users)) {
            return null;
        }
        return users.get(0);
    }

    @Override
    public User findByOpenId(String openId) {
        UserVO userVO = new UserVO();
        userVO.setOpenId(openId);
        List<User> users = userDao.findByParameter(userVO);
        if (CollectionUtil.isEmpty(users)) {
            return null;
        }
        return users.get(0);
    }

    @Override
    public User findByImId(String imId) {
        UserVO userVO = new UserVO();
        userVO.setImId(imId);
        List<User> userList = userDao.findByParameter(userVO);
        if (CollectionUtil.isEmpty(userList)) {
            return null;
        }
        return userList.get(0);
    }

    @Override
    public List<User> findByImIds(String imIds) {
        String[] imIdArr = imIds.split(Constant.DEFAULT_SPLIT_SEPARATOR);
        List<User> userList = new ArrayList<User>();
        UserVO userVO = new UserVO();
        if (imIdArr.length > 0) {
            for (int i = 0; i < imIdArr.length; i++) {
                userVO.setImId(imIdArr[i]);
                List<User> users = userDao.findByParameter(userVO);
                if (users.size() > 0) {
                    User user = users.get(0);
                    userList.add(user);
                }
            }
        }
        return userList;
    }


    @Override
    public void lock(int id) {
        User user = findById(id);
        user.setUpdateTime(new Date());
        user.setStatus(UserStatusEnum.BANNED.getType());
        userDao.update(user);
        SubjectUtil.setCurrentUser(user);
    }

    @Override
    public void unlock(int id) {
        User user = findById(id);
        user.setUpdateTime(new Date());
        user.setStatus(UserStatusEnum.NORMAL.getType());
        userDao.update(user);
        SubjectUtil.setCurrentUser(user);
    }

    @Override
    public PageInfo<User> list(UserVO userVO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "create_time DESC");
        List<User> list = userDao.findByParameter(userVO);
        return new PageInfo(list);
    }

    @Override
    public User save(UserVO userVO) {
        User user = new User();
        BeanUtil.copyProperties(userVO, user);
        user.setStatus(UserStatusEnum.NORMAL.getType());//默认账户解封状态
        user.setType(UserTypeEnum.GENERAL_USER.getType());//默认普通用户
        user.setUserInfoAuth(UserInfoAuthStatusEnum.NOT_PERFECT.getType());//默认未审核
        user.setBalance(Constant.DEFAULT_BALANCE);
        user.setScoreAvg(Constant.DEFAULT_SCORE_AVG);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userDao.create(user);
        SubjectUtil.setCurrentUser(user);
        return user;
    }

    @Override
    public User getCurrentUser() {
        Object userObj = SubjectUtil.getCurrentUser();
        if (null == userObj) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        if (userObj instanceof User) {
            return (User) userObj;
        } else {
            return null;
        }
    }

    public void updateRedisUser(User user) {
        String token = SubjectUtil.getToken();
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap = BeanUtil.beanToMap(user);
        redisOpenService.hset(RedisKeyEnum.PLAY_TOKEN.generateKey(token), userMap);
    }

    @Override
    public Boolean isCurrentUser(Integer userId) {
        User currentUser = getCurrentUser();
        if (currentUser.getId().equals(userId)) {
            return true;
        }
        throw new ServiceErrorException("用户不匹配!");
    }

    @Override
    public String getShareCard(Integer userId, Integer techAuthId, String scene, String page) throws WxErrorException, IOException {

        UserInfoVO userInfoVO = userInfoAuthService.findUserTechCardByUserId(userId, techAuthId);
        //查询文案信息
        SharingVO sharingVO = new SharingVO();
        sharingVO.setShareType(ShareTypeEnum.TECH_AUTH.getType());
        sharingVO.setGender(userInfoVO.getGender());
        sharingVO.setStatus(true);
        List<Sharing> shareList = sharingService.findByParam(sharingVO);
        String shareContent = "";
        if (!CollectionUtil.isEmpty(shareList)) {
            shareContent = shareList.get(0).getContent();
        }
        String codeUrl = wxCodeService.create(scene, page);
        Map<String, String> contentMap = getContentMap(userInfoVO, shareContent, codeUrl);
        String shareCardUrl = imgUtil.create(contentMap);
        return shareCardUrl;
    }

    private Map<String, String> getContentMap(UserInfoVO userInfoVO, String shareContent, String codeUrl) {
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("nickname", userInfoVO.getNickName());
        contentMap.put("gender", userInfoVO.getGender().toString());
        contentMap.put("age", userInfoVO.getAge().toString());
        StringBuilder sb = new StringBuilder();
        sb.append(userInfoVO.getUserTechAuthVO().getCategoryName());
        sb.append("陪玩 ");
        sb.append("段位 ");
        sb.append(userInfoVO.getUserTechAuthVO().getDanInfo().getValue());
        sb.append(" 标签");
        String tagStr = "";
        List<String> tagList = userInfoVO.getTags();
        for (String str : tagList) {
            tagStr += " " + str;
        }
        sb.append(tagStr);
        contentMap.put("techAndTag", sb.toString());
        JSONObject jo = new JSONObject(shareContent);
        contentMap.put("title", jo.getStr("title"));
        contentMap.put("content", jo.getStr("content"));
        contentMap.put("mainPicUrl", userInfoVO.getMainPhotoUrl());
        contentMap.put("codeUrl", codeUrl);
        return contentMap;
    }

}
