package com.fulu.game.core.service.impl;

import cn.hutool.json.JSONObject;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.exception.UserAuthException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.ImgUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserDao;
import com.fulu.game.core.entity.Sharing;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserInfoAuth;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.service.*;
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
    private ProductService productService;
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


    public void checkUserInfoAuthStatus(Integer userId) {
        User user = findById(userId);
        UserInfoAuth userInfoAuth = userInfoAuthService.findByUserId(userId);
        if (userInfoAuth == null) {
            throw new UserAuthException(UserAuthException.ExceptionCode.NOT_EXIST_USER_AUTH);
        }
        if (user.getUserInfoAuth().equals(UserInfoAuthStatusEnum.NOT_PERFECT.getType())) {
            throw new UserAuthException(UserAuthException.ExceptionCode.SERVICE_USER_REJECT);
        }
        if (user.getUserInfoAuth().equals(UserInfoAuthStatusEnum.FREEZE.getType())) {
            throw new UserAuthException(UserAuthException.ExceptionCode.SERVICE_USER_FREEZE);
        }
    }

    @Override
    public String getTechAuthCard(Integer techAuthId, String scene, String page) throws WxErrorException, IOException {

        UserInfoVO userInfoVO = userInfoAuthService.findUserTechCardByUserId(techAuthId);
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
        Map<String, String> contentMap = getTechAuthContentMap(userInfoVO, shareContent, codeUrl);
        String shareCardUrl = imgUtil.createTechAuth(contentMap);
        return shareCardUrl;
    }

    private Map<String, String> getTechAuthContentMap(UserInfoVO userInfoVO, String shareContent, String codeUrl) {
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

    @Override
    public String getTechShareCard(String scene, Integer productId) throws WxErrorException, IOException {
        ProductDetailsVO productDetailsVO = productService.findDetailsByProductId(productId);

        //查询文案信息
        SharingVO sharingVO = new SharingVO();
        sharingVO.setShareType(ShareTypeEnum.ORDER_SET.getType());
        sharingVO.setGender(productDetailsVO.getUserInfo().getGender());
        sharingVO.setStatus(true);
        List<Sharing> shareList = sharingService.findByParam(sharingVO);
        String shareStr = "";
        if (!CollectionUtil.isEmpty(shareList)) {
            shareStr = shareList.get(0).getContent();
        }
        String codeUrl = wxCodeService.create(scene, PagePathEnum.TECH_SHARE_CARD.getPagePath());
        Map<String, String> contentMap = getTechCardContentMap(productDetailsVO, shareStr, codeUrl);
        String shareCardUrl = imgUtil.createTechCard(contentMap);
        return shareCardUrl;
    }

    private Map<String, String> getTechCardContentMap(ProductDetailsVO pdVO, String shareStr, String codeUrl) {
        Map<String, String> contentMap = new HashMap<>();
        UserInfoVO userInfoVO = pdVO.getUserInfo();
        //组装个人信息
        if (null != userInfoVO) {
            contentMap.put("mainPicUrl", userInfoVO.getMainPhotoUrl());
            contentMap.put("nickname", userInfoVO.getNickName());
            contentMap.put("gender", userInfoVO.getGender().toString());
            contentMap.put("age", userInfoVO.getAge().toString());
            contentMap.put("city", userInfoVO.getCity());
            String tagStr = "";
            List<String> tagList = userInfoVO.getTags();
            for (String str : tagList) {
                tagStr += " " + str;
            }
            contentMap.put("tagStr", tagStr);
        }

        //主商品信息
        contentMap.put("mainTechIconUrl", pdVO.getCategoryIcon());
        String mainTech = pdVO.getProductName() + " 陪玩 " ;
        contentMap.put("mainTechName", mainTech);

        String mainPrice = "￥" + pdVO.getPrice() + "元/" + pdVO.getUnit();
        contentMap.put("mainPrice", mainPrice);

        String mainTechTagStr = "";
        List<String> techTagList = pdVO.getTechTags();
        for (String str : techTagList) {
            mainTechTagStr += " " + str;
        }
        contentMap.put("mainTechTagStr", mainTechTagStr);

        //次商品信息
        List<ProductVO> otherProductList = productService.findOthersByproductId(pdVO.getId());
        if (!CollectionUtil.isEmpty(otherProductList)) {
            ProductVO productVO = otherProductList.get(0);
            contentMap.put("seccondTechIconUrl", productVO.getCategoryIcon());
            String seccondTech = productVO.getProductName() + " 陪玩 ";
            contentMap.put("seccondTech", seccondTech);
            String secondPrice = "￥" + productVO.getPrice() + "元/" + productVO.getUnit();
            contentMap.put("secondPrice", secondPrice);
            String secondTechTagStr = "";
            List<String> secondTechTagList = productVO.getTechTags();
            for (String str : secondTechTagList) {
                secondTechTagStr += " " + str;
            }
            contentMap.put("secondTechTagStr", secondTechTagStr);
        }
        contentMap.put("shareStr", shareStr);
        contentMap.put("codeUrl", codeUrl);
        return contentMap;
    }

}
