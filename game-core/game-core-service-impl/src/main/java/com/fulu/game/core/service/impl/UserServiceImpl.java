package com.fulu.game.core.service.impl;

import cn.hutool.json.JSONObject;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.ImgException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.exception.UserAuthException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.ImgUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoleilu.hutool.util.BeanUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service("userService")
@Slf4j
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
    private AdminService adminService;
    @Autowired
    private ImgUtil imgUtil;

    @Override
    public ICommonDao<User, Integer> getDao() {
        return userDao;
    }

    @Override
    public User findByMobile(String mobile) {
        if (mobile == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        userVO.setMobile(mobile);
        List<User> users = userDao.findByParameter(userVO);
        if (CollectionUtil.isEmpty(users)) {
            return null;
        }
        return users.get(0);
    }

    @Override
    public List<User> findAllServeUser() {
        UserVO params = new UserVO();
        params.setType(UserTypeEnum.ACCOMPANY_PLAYER.getType());
        List<User> users = userDao.findByParameter(params);
        return users;
    }


    @Override
    public User findByOpenId(String openId) {
        if (openId == null) {
            return null;
        }
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
        if (imId == null) {
            return null;
        }
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
    public List<User> findAllNormalUser() {
        UserVO param = new UserVO();
        param.setStatus(UserStatusEnum.NORMAL.getType());
        List<User> users = userDao.findByParameter(param);
        return users;
    }

    @Override
    public Integer countAllNormalUser() {
        UserVO param = new UserVO();
        param.setStatus(UserStatusEnum.NORMAL.getType());
        int count = userDao.countByParameter(param);
        return count;
    }

    @Override
    public List<User> findByUserIds(List<Integer> userIds) {
        if (userIds == null) {
            return new ArrayList<>();
        }
        return userDao.findByUserIds(userIds);
    }


    @Override
    public void lock(int id) {
        Admin admin = adminService.getCurrentUser();
        User user = findById(id);
        user.setUpdateTime(new Date());
        user.setStatus(UserStatusEnum.BANNED.getType());
        userDao.update(user);
        SubjectUtil.setCurrentUser(user);
        log.info("用户id:{}被管理员id:{}封禁", id, admin.getId());
    }

    @Override
    public void unlock(int id) {
        Admin admin = adminService.getCurrentUser();
        User user = findById(id);
        user.setUpdateTime(new Date());
        user.setStatus(UserStatusEnum.NORMAL.getType());
        userDao.update(user);
        SubjectUtil.setCurrentUser(user);
        log.info("用户id:{}被管理员id:{}解封", id, admin.getId());
    }

    @Override
    public PageInfo<User> list(UserVO userVO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "create_time DESC");
        List<User> list = userDao.findByParameter(userVO);
        return new PageInfo(list);
    }

    @Override
    public User createNewUser(UserVO userVO) {
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
    public User createNewUser(String openId, Integer sourceId) {
        UserVO user = new UserVO();
        user.setSourceId(sourceId);
        user.setOpenId(openId);
        return createNewUser(user);
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
    public String getTechAuthCard(Integer techAuthId, String scene) throws WxErrorException, IOException {

        UserInfoVO userInfoVO = userInfoAuthService.findUserTechCardByUserId(techAuthId);
        if (null == userInfoVO) {
            log.error("技能认证分享-查不到该用户信息");
            throw new UserException(UserException.ExceptionCode.USER_INFO_NULL_EXCEPTION);
        }
        if (StringUtils.isEmpty(userInfoVO.getMainPhotoUrl())) {
            log.error("技能认证分享-未上传主图");
            throw new UserException(UserException.ExceptionCode.MAINPHOTO_NOT_EXIST_EXCEPTION);
        }
        //查询文案信息
        SharingVO sharingVO = new SharingVO();
        sharingVO.setShareType(ShareTypeEnum.TECH_AUTH.getType());
        sharingVO.setGender(null == userInfoVO.getGender() ? GenderEnum.ASEXUALITY.getType() : userInfoVO.getGender());
        sharingVO.setStatus(true);
        List<Sharing> shareList = sharingService.findByParam(sharingVO);
        String shareContent = "";
        if (!CollectionUtil.isEmpty(shareList)) {
            shareContent = shareList.get(0).getContent();
            if (StringUtils.isEmpty(shareContent)) {
                log.error("技能认证分享-文案为空");
                throw new ImgException(ImgException.ExceptionCode.SHARECONTENT_BLANK);
            }
        } else {
            log.error("技能认证分享-未查询到对应文案");
            throw new ImgException(ImgException.ExceptionCode.SHARE_NOT_EXSISTS);
        }
        String codeUrl = wxCodeService.create(scene, PagePathEnum.TECH_AUTH_CARD.getPagePath());
        ImgUtil.CardImg cardImg = getTechAuthContentMap(userInfoVO, shareContent, codeUrl);
        String shareCardUrl = imgUtil.createTechAuth(cardImg);
        return shareCardUrl;
    }

    @Override
    public String getTechShareCard(String scene, Integer productId) throws WxErrorException, IOException {
        ProductDetailsVO productDetailsVO = productService.findDetailsByProductId(productId);
        UserInfoVO userInfoVO = productDetailsVO.getUserInfo();
        if (null == userInfoVO) {
            log.error("技能认证分享-查不到该用户信息");
            throw new UserException(UserException.ExceptionCode.USER_INFO_NULL_EXCEPTION);
        }
        if (StringUtils.isEmpty(userInfoVO.getMainPhotoUrl())) {
            log.error("技能认证分享-未上传主图");
            throw new UserException(UserException.ExceptionCode.MAINPHOTO_NOT_EXIST_EXCEPTION);
        }
        //查询文案信息
        SharingVO sharingVO = new SharingVO();
        sharingVO.setShareType(ShareTypeEnum.ORDER_SET.getType());
        sharingVO.setGender(productDetailsVO.getUserInfo().getGender());
        sharingVO.setStatus(true);
        List<Sharing> shareList = sharingService.findByParam(sharingVO);
        String shareStr = "";
        if (!CollectionUtil.isEmpty(shareList)) {
            shareStr = shareList.get(0).getContent();
            if (StringUtils.isEmpty(shareStr)) {
                log.error("技能认证分享-文案为空");
                throw new ImgException(ImgException.ExceptionCode.SHARECONTENT_BLANK);
            }
        } else {
            log.error("技能认证分享-未查询到对应文案");
            throw new ImgException(ImgException.ExceptionCode.SHARE_NOT_EXSISTS);
        }
        String codeUrl = wxCodeService.create(scene, PagePathEnum.TECH_SHARE_CARD.getPagePath());
        ImgUtil.CardImg cardImg = getTechCardContentMap(productDetailsVO, shareStr, codeUrl);
        String shareCardUrl = imgUtil.createTechCard(cardImg);
        return shareCardUrl;
    }

    private ImgUtil.CardImg getTechAuthContentMap(UserInfoVO userInfoVO, String shareContent, String codeUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append(userInfoVO.getUserTechAuthVO().getCategoryName());
        sb.append("陪玩｜");
        sb.append("段位:");
        sb.append(userInfoVO.getUserTechAuthVO().getDanInfo().getValue()).append("｜");
        sb.append("标签:");
        String tagStr = "";
        List<String> tagList = userInfoVO.getTags();
        for (String str : tagList) {
            tagStr += str + "、";
        }
        if (!"".equals(tagStr)) {
            tagStr = tagStr.substring(0, tagStr.length() - 1);
        }
        sb.append(tagStr);
        String title = "";
        String content = "";
        try {
            JSONObject jo = new JSONObject(shareContent);
            title = jo.getStr("title");
            content = jo.getStr("content");
        } catch (Exception e) {
            throw new ImgException(ImgException.ExceptionCode.JSONFORMAT_ERROR);
        }
        return ImgUtil.CardImg.builder()
                .nickname(null == userInfoVO.getNickName() ? "陪玩师" : userInfoVO.getNickName())
                .gender(null == userInfoVO.getGender() ? GenderEnum.ASEXUALITY.getType() : userInfoVO.getGender())
                .age(userInfoVO.getAge())
                .techStr(sb.toString())
                .mainPicUrl(userInfoVO.getMainPhotoUrl())
                .codeUrl(codeUrl)
                .shareTitle(title)
                .shareContent(content).build();
    }

    private ImgUtil.CardImg getTechCardContentMap(ProductDetailsVO pdVO, String shareStr, String codeUrl) {

        UserInfoVO userInfoVO = pdVO.getUserInfo();
        String personTag1 = null;
        String personTag2 = null;
        List<String> tagList = userInfoVO.getTags();
        if (!CollectionUtil.isEmpty(tagList)) {
            personTag1 = tagList.get(0);
            if (tagList.size() > 1) {
                personTag2 = tagList.get(1);
            }
        }
        //主商品信息
        Map<String, String> mainTechMap = new HashMap<>();
        if (StringUtils.isEmpty(pdVO.getCategoryIcon())) {
            throw new ImgException(ImgException.ExceptionCode.CATEGORY_ICON_URL);
        }
        mainTechMap.put("techIconUrl", pdVO.getCategoryIcon());
        String mainTech = pdVO.getProductName();
        mainTechMap.put("techName", mainTech);

        String mainPrice = "￥" + pdVO.getPrice() + "元/" + pdVO.getUnit();
        mainTechMap.put("price", mainPrice);

        String techTag1 = null;
        String techTag2 = null;
        List<String> techTagList = pdVO.getTechTags();
        if (!CollectionUtil.isEmpty(techTagList)) {
            techTag1 = techTagList.get(0);
            if (techTagList.size() > 1) {
                techTag2 = techTagList.get(1);
            }
        }
        mainTechMap.put("techTag1", techTag1);
        mainTechMap.put("techTag2", techTag2);

        //次商品信息
        List<ProductVO> otherProductList = productService.findOthersByproductId(pdVO.getId());
        Map<String, String> secTechMap = new HashMap<>();
        if (!CollectionUtil.isEmpty(otherProductList)) {
            ProductVO productVO = otherProductList.get(0);
            secTechMap.put("techIconUrl", productVO.getCategoryIcon());
            String secTechName = productVO.getProductName();
            secTechMap.put("techName", secTechName);
            String price = "￥" + productVO.getPrice() + "元/" + productVO.getUnit();
            secTechMap.put("price", price);
            String secTechTag1 = null;
            String secTechTag2 = null;
            List<String> secTechTagList = productVO.getTechTags();
            if (!CollectionUtil.isEmpty(secTechTagList)) {
                secTechTag1 = secTechTagList.get(0);
                if (secTechTagList.size() > 1) {
                    secTechTag2 = secTechTagList.get(1);
                }
            }
            secTechMap.put("techTag1", secTechTag1);
            secTechMap.put("techTag2", secTechTag2);
        }
        return ImgUtil.CardImg.builder()
                .nickname(null == userInfoVO.getNickName() ? "陪玩师" : userInfoVO.getNickName())
                .gender(null == userInfoVO.getGender() ? GenderEnum.ASEXUALITY.getType() : userInfoVO.getGender())
                .age(userInfoVO.getAge())
                .city(null == userInfoVO.getCity() ? Constant.DEFAULT_CITY : userInfoVO.getCity())
                .mainPicUrl(userInfoVO.getMainPhotoUrl())
                .codeUrl(codeUrl)
                .shareStr(shareStr)
                .personTag1(personTag1)
                .personTag2(personTag2)
                .mainTech(mainTechMap)
                .secTech(secTechMap)
                .build();
    }

    @Override
    public List<ImUser> findImNullUser() {
        return userDao.findImNullUser();
    }

    @Override
    public void bindIm(ImUser imUser){
        if (null == imUser || null == imUser.getUserId()){
            return;
        }
        User user = userDao.findById(imUser.getUserId());
        user.setImId(imUser.getUsername());
        user.setImPsw(imUser.getImPsw());
        user.setUpdateTime(new Date());
        userDao.update(user);
    }

}
