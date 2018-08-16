package com.fulu.game.core.service.impl;

import cn.hutool.json.JSONObject;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.ImgException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.exception.UserAuthException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.threadpool.SpringThreadPoolExecutor;
import com.fulu.game.common.utils.ImgUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.aop.UserScore;
import com.fulu.game.core.service.impl.coupon.DefaultCouponServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Autowired
    private CouponGroupService couponGroupService;

    @Autowired
    private DefaultCouponServiceImpl couponService;

    @Autowired
    private SpringThreadPoolExecutor springThreadPoolExecutor;

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
    public User findByUnionId(String unionId) {
        if (unionId == null) {
            return null;
        }
        UserVO params = new UserVO();
        params.setUnionId(unionId);
        List<User> users = userDao.findByParameter(params);
        if (users.isEmpty()) {
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
    public List<User> findByLoginTime(Integer userType, Date startTime, Date endTime) {
        UserVO userVO = new UserVO();
        userVO.setType(userType);
        userVO.setStartTime(startTime);
        userVO.setEndTime(endTime);
        return userDao.findByExportParam(userVO);
    }


    @Override
    public User findByOpenId(String openId, WechatEcoEnum wechatEcoEnum) {
        if (openId == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        if (WechatEcoEnum.PLAY.equals(wechatEcoEnum)) {
            userVO.setOpenId(openId);
        } else if (WechatEcoEnum.POINT.equals(wechatEcoEnum)) {
            userVO.setPointOpenId(openId);
        } else {
            return null;
        }
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
    public Integer countAllNormalUser() {
        UserVO param = new UserVO();
        param.setStatus(UserStatusEnum.NORMAL.getType());
        int count = userDao.countByParameter(param);
        return count;
    }

    @Override
    public List<User> findByUserIds(List<Integer> userIds, Boolean disabled) {
        if (CollectionUtil.isEmpty(userIds)) {
            return new ArrayList<>();
        }
        return userDao.findByUserIds(userIds, disabled);
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
    public PageInfo<UserVO> list(UserVO userVO, Integer pageNum, Integer pageSize) {
        String orderBy;
        if (StringUtils.isBlank(userVO.getOrderBy())) {
            orderBy = "u.create_time desc";
        } else {
            orderBy = userVO.getOrderBy();
        }
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<UserVO> list = userDao.findBySearch(userVO);
        return new PageInfo(list);
    }


    public User createThirdPartyUser(Integer sourceId,String ip){
        User user = new User();
        user.setRegistIp(ip);
        user.setSourceId(sourceId);
        user.setNickname("游客");
        user.setHeadPortraitsUrl("http://game-play.oss-cn-hangzhou.aliyuncs.com/2018/8/16/939794a1be2d46e9955db88716e24e54.png");
        return createNewUser(user);
    }



    public User createNewUser(User user) {
        user.setStatus(UserStatusEnum.NORMAL.getType());//默认账户解封状态
        user.setType(UserTypeEnum.GENERAL_USER.getType());//默认普通用户
        user.setUserInfoAuth(UserInfoAuthStatusEnum.NOT_PERFECT.getType());//默认未审核
        user.setBalance(Constant.DEFAULT_BALANCE);
        user.setScoreAvg(Constant.DEFAULT_SCORE_AVG);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userDao.create(user);
        return user;
    }


    @Override
    public User createNewUser(WechatEcoEnum wechatEcoEnum, String openId, Integer sourceId, String host) {
        User user = new User();
        user.setRegistIp(host);
        if (WechatEcoEnum.PLAY.equals(wechatEcoEnum)) {
            user.setOpenId(openId);
        } else if (WechatEcoEnum.POINT.equals(wechatEcoEnum)) {
            user.setPointOpenId(openId);
        } else {
            throw new UserException(UserException.ExceptionCode.NO_WECHATECO_EXCEPTION);
        }
        user.setSourceId(sourceId);

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

    @Override
    @UserScore(type = UserScoreEnum.USER_LOGIN)
    public User updateUserIpAndLastTime(String ip) {
        User user = getCurrentUser();
        user.setLoginIp(ip);
        user.setLoginTime(new Date());
        update(user);
        return user;
    }

    @Override
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
    public void checkUserInfoAuthStatus(Integer userId, Integer... ignoreAuthStatus) {
        User user = findById(userId);
        UserInfoAuth userInfoAuth = userInfoAuthService.findByUserId(userId);
        if (ignoreAuthStatus != null) {
            if (Arrays.asList(ignoreAuthStatus).contains(user.getUserInfoAuth())) {
                return;
            }
        }
        if (userInfoAuth == null) {
            throw new UserAuthException(UserAuthException.ExceptionCode.NOT_EXIST_USER_AUTH);
        }
        if (userInfoAuth.getMainPicUrl() == null) {
            throw new UserAuthException(UserAuthException.ExceptionCode.NOT_EXIST_USER_AUTH);
        }
//        if (user.getUserInfoAuth().equals(UserInfoAuthStatusEnum.NOT_PERFECT.getType())) {
//            throw new UserAuthException(UserAuthException.ExceptionCode.SERVICE_USER_REJECT);
//        }
//        if (user.getUserInfoAuth().equals(UserInfoAuthStatusEnum.ALREADY_PERFECT.getType())) {
//            throw new UserAuthException(UserAuthException.ExceptionCode.SERVICE_USER_AUTHING);
//        }
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
        int gender = GenderEnum.MALE.getType() == userInfoVO.getGender() ? userInfoVO.getGender() : GenderEnum.LADY.getType();
        userInfoVO.setGender(gender);
        //查询文案信息
        SharingVO sharingVO = new SharingVO();
        sharingVO.setShareType(ShareTypeEnum.TECH_AUTH.getType());
        sharingVO.setGender(gender);
        sharingVO.setStatus(true);
        List<Sharing> shareList = sharingService.findByParam(sharingVO);
        String shareContent;
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
        String codeUrl = wxCodeService.create(scene, WechatPagePathEnum.TECH_AUTH_CARD.getPlayPagePath());
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
        int gender = GenderEnum.MALE.getType() == userInfoVO.getGender() ? userInfoVO.getGender() : GenderEnum.LADY.getType();
        userInfoVO.setGender(gender);
        //查询文案信息
        SharingVO sharingVO = new SharingVO();
        sharingVO.setShareType(ShareTypeEnum.ORDER_SET.getType());
        sharingVO.setGender(gender);
        sharingVO.setStatus(true);
        List<Sharing> shareList = sharingService.findByParam(sharingVO);
        String shareStr;
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
        String codeUrl = wxCodeService.create(scene, WechatPagePathEnum.TECH_SHARE_CARD.getPlayPagePath());
        ImgUtil.CardImg cardImg = getTechCardContentMap(productDetailsVO, shareStr, codeUrl);
        String shareCardUrl = imgUtil.createTechCard(cardImg);
        return shareCardUrl;
    }

    private ImgUtil.CardImg getTechAuthContentMap(UserInfoVO userInfoVO, String shareContent, String codeUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append(userInfoVO.getUserTechAuthVO().getCategoryName());
        sb.append("陪玩｜");
        if (userInfoVO.getUserTechAuthVO().getDanInfo() != null) {
            sb.append("段位:");
            sb.append(userInfoVO.getUserTechAuthVO().getDanInfo().getValue()).append("｜");
        }
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
        String title;
        String content;
        try {
            JSONObject jo = new JSONObject(shareContent);
            title = jo.getStr("title");
            content = jo.getStr("content");
        } catch (Exception e) {
            throw new ImgException(ImgException.ExceptionCode.JSONFORMAT_ERROR);
        }
        return ImgUtil.CardImg.builder()
                .nickname(null == userInfoVO.getNickName() ? "陪玩师" : userInfoVO.getNickName())
                .gender(userInfoVO.getGender())
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
                .gender(userInfoVO.getGender())
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
    public void bindIm(ImUser imUser) {
        if (null == imUser || null == imUser.getUserId()) {
            return;
        }
        User user = userDao.findById(imUser.getUserId());
        user.setImId(imUser.getUsername());
        user.setImPsw(imUser.getImPsw());
        user.setUpdateTime(new Date());
        userDao.update(user);
    }

    @Override
    public User updateUnionUser(UserVO user, WechatEcoEnum wechatEcoEnum, String ipStr) {
        log.info("调用updateUnionUser方法:user:{}", user);
        User unionUser = findByUnionId(user.getUnionId());
        if (unionUser == null) {
            log.info("unionUser为空更新自己的unionId:{}", user.getUnionId());
            update(user);
            updateRedisUser(user);
            return user;
        }
        if (unionUser.getId().equals(user.getId())) {
            log.info("该用户已经存在unionUser:{}", unionUser);
            update(user);
            updateRedisUser(user);
            return user;
        }
        if (WechatEcoEnum.POINT.equals(wechatEcoEnum)) {
            log.info("判断存在开黑用户信息，更新unionUser:{}", unionUser);
            unionUser.setPointOpenId(user.getPointOpenId());
            //删除上分的用户
            user.setPointOpenId(unionUser.getId() + "-" + user.getPointOpenId() + "-" + System.currentTimeMillis());
            user.setUnionId(unionUser.getId() + "-" + user.getUnionId() + "-" + System.currentTimeMillis());
            update(user);
            //更新陪玩的用户
            update(unionUser);
            updateRedisUser(unionUser);
            log.info("判断存在开黑用户信息，更新user:{}", user);
            return unionUser;
        } else {
            log.info("判断存在上分的用户信息，unionUser:{}", unionUser);
            user.setPointOpenId(unionUser.getPointOpenId());
            //删除上分的用户
            unionUser.setPointOpenId(user.getId() + "-" + unionUser.getPointOpenId() + "-" + System.currentTimeMillis());
            unionUser.setUnionId(user.getId() + "-" + unionUser.getUnionId() + "-" + System.currentTimeMillis());
            update(unionUser);
            //更新陪玩的用户
            update(user);
            updateRedisUser(user);
            log.info("判断存在上分的用户信息，更新user:{}", user);

            //新线程发放优惠券（避免事务问题：当前方法的事务和优惠券发放的事务，因为都涉及到t_user表的操作，可能造成数据库死锁）
            springThreadPoolExecutor.getAsyncExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    generateCouponForNewPointUser(user, ipStr);
                }
            });

            user.setCoupouStatus(Constant.SEND_COUPOU_SUCCESS);
            return user;
        }
    }

    /**
     * 发放优惠券
     *
     * @param user  用户Bean
     * @param ipStr 用户ip
     * @return 是否发放成功
     */
    private boolean generateCouponForNewPointUser(User user, String ipStr) {
        if (user == null) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }

        Coupon coupon = couponService.generateCoupon(Constant.NEW_POINT_USER_COUPON_GROUP_REDEEM_CODE,
                user.getId(), DateUtil.date(), ipStr);
        if (coupon == null) {
            throw new ServiceErrorException("发放优惠券失败！");
        }
        return true;
    }

    @Override
    public List<UserVO> findVOByUserIds(List<Integer> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return new ArrayList<>();
        }
        return userDao.findUserVOByUserIds(userIds);
    }

    @Override
    public Integer findUserScoreByUpdate(Integer userId) {
        return userDao.findUserScoreByUpdate(userId);
    }

    @Override
    public boolean getUserCouponStatus(User user) {
        if (user == null) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        log.info("获取新用户userId:{}优惠券领取状态", user.getId());

        String openId = user.getOpenId();
        CouponGroup couponGroup = couponGroupService.findByRedeemCode(Constant.NEW_POINT_USER_COUPON_GROUP_REDEEM_CODE);
        if (couponGroup == null) {
            throw new ServiceErrorException("查询不到优惠券！");
        }

        if (StringUtils.isBlank(openId)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isOldUser(Integer userId) {
        return userDao.countUserOrder(userId) > 0;

    }
}