package com.fulu.game.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.fulu.game.common.Constant;
import com.fulu.game.common.enums.*;
import com.fulu.game.common.exception.ImgException;
import com.fulu.game.common.exception.ServiceErrorException;
import com.fulu.game.common.exception.UserAuthException;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.threadpool.SpringThreadPoolExecutor;
import com.fulu.game.common.utils.GenIdUtil;
import com.fulu.game.common.utils.ImgUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.entity.vo.searchVO.UserInfoAuthSearchVO;
import com.fulu.game.core.search.component.UserSearchComponent;
import com.fulu.game.core.search.domain.UserDoc;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.aop.UserScore;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service("userService")
@Slf4j
public class UserServiceImpl extends AbsCommonService<User, Integer> implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Qualifier(value = "userInfoAuthServiceImpl")
    @Autowired
    private UserInfoAuthServiceImpl userInfoAuthService;
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
    private VirtualDetailsService virtualDetailsService;

    @Autowired
    private UserInfoAuthFileService userInfoAuthFileService;

    @Autowired
    private AccessLogService accessLogService;

    @Autowired
    private DynamicService dynamicService;

    @Autowired
    private SpringThreadPoolExecutor springThreadPoolExecutor;

    @Autowired
    private AdminImLogService adminImLogService;

    @Autowired
    private MoneyDetailsService moneyDetailsService;

    @Qualifier(value = "userTechAuthServiceImpl")
    @Autowired
    private UserTechAuthServiceImpl userTechAuthService;

    @Autowired
    private UserSearchComponent userSearchComponent;


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
    public User findByOpenId(String openId, PlatformEcoEnum platformEcoEnum) {
        if (openId == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        switch (platformEcoEnum) {
            case PLAY:
                userVO.setOpenId(openId);
                break;
            case POINT:
                userVO.setPointOpenId(openId);
                break;
            case MP:
                userVO.setPublicOpenId(openId);
                break;
            default:
                break;
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
        return userDao.countByParameter(param);
    }

    @Override
    public Integer countAllServiceUser() {
        UserVO params = new UserVO();
        params.setStatus(UserStatusEnum.NORMAL.getType());
        params.setType(UserTypeEnum.ACCOMPANY_PLAYER.getType());
        return userDao.countByParameter(params);
    }

    @Override
    public List<User> findByUserIds(List<Integer> userIds, Boolean disabled) {
        if (CollectionUtil.isEmpty(userIds)) {
            return new ArrayList<>();
        }
        return userDao.findByUserIds(userIds, disabled);
    }


    @Override
    public boolean lock(int id) {
        Admin admin = adminService.getCurrentUser();
        User user = findById(id);
        user.setUpdateTime(new Date());
        user.setStatus(UserStatusEnum.BANNED.getType());
        userDao.update(user);
        log.info("用户id:{}被管理员id:{}封禁", id, admin.getId());
        return removeUserLoginToken(user.getId());
    }

    @Override
    public void unlock(int id) {
        Admin admin = adminService.getCurrentUser();
        User user = findById(id);
        user.setUpdateTime(new Date());
        user.setStatus(UserStatusEnum.NORMAL.getType());
        userDao.update(user);
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

        if (pageNum != null && pageSize != null) {
            PageHelper.startPage(pageNum, pageSize, orderBy);
        } else {
            PageHelper.orderBy(orderBy);
        }

        List<UserVO> list = userDao.findBySearch(userVO);
        return new PageInfo(list);
    }

    @Override
    public List<UserVO> list(UserInfoAuthSearchVO userInfoAuthSearchVO) {
        String orderBy;
        if (StringUtils.isBlank(userInfoAuthSearchVO.getOrderBy())) {
            orderBy = "u.create_time desc";
        } else {
            orderBy = userInfoAuthSearchVO.getOrderBy();
        }

        PageHelper.orderBy(orderBy);
        return userDao.findServiceUserBySearch(userInfoAuthSearchVO);
    }

    @Override
    public User createThirdPartyUser(Integer sourceId, String ip) {
        User user = new User();
        user.setRegistIp(ip);
        user.setSourceId(sourceId);
        while (true) {
            String nickname = "游客" + RandomUtil.randomNumbers(7);
            UserVO userVO = new UserVO();
            userVO.setNickname(nickname);
            List<User> userList = userDao.findByParameter(userVO);
            if (CollectionUtils.isEmpty(userList)) {
                user.setNickname(nickname);
                break;
            }
        }
        user.setHeadPortraitsUrl("http://game-play.oss-cn-hangzhou.aliyuncs.com/2018/9/28/2a0696764cf141a9b75d0afae7d09570.png");
        return createNewUser(user);
    }

    private User createNewUser(User user) {
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
    public User createNewUser(PlatformEcoEnum platformEcoEnum, String openId, Integer sourceId, String host) {
        User user = new User();
        user.setRegistIp(host);
        if (PlatformEcoEnum.PLAY.equals(platformEcoEnum)) {
            user.setOpenId(openId);
        } else if (PlatformEcoEnum.POINT.equals(platformEcoEnum)) {
            user.setPointOpenId(openId);
        } else {
            throw new UserException(UserException.ExceptionCode.NO_WECHATECO_EXCEPTION);
        }
        user.setSourceId(sourceId);

        return createNewUser(user);
    }

    @Override
    public User createNewUser(String mobile, String host) {
        User user = new User();
        user.setRegistIp(host);
        user.setMobile(mobile);
        user.setNickname("游客"+ GenIdUtil.GetVisitorNo());
        return createNewUser(user);
    }

    @Override
    public User createNewUser(String mobile, String mpOpenId, String unionId, String host) {
        User user = new User();
        user.setPublicOpenId(mpOpenId);
        user.setMobile(mobile);
        user.setUnionId(unionId);
        user.setRegistIp(host);
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
        if (ignoreAuthStatus != null) {
            if (Arrays.asList(ignoreAuthStatus).contains(user.getUserInfoAuth())) {
                return;
            }
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
    public User updateUnionUser(UserVO user, PlatformEcoEnum platformEcoEnum, String ipStr) {
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
        if (PlatformEcoEnum.POINT.equals(platformEcoEnum)) {
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


            user.setCoupouStatus(Constant.SEND_COUPOU_SUCCESS);
            return user;
        }
    }


    @Override
    public List<UserVO> findVOByUserIds(List<Integer> userIds) {
        //todo 这个地方要改成userAuthInfo的VO
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

    @Override
    public User modifyCharm(User user, Integer charm) {
        if (user == null) {
            return null;
        }

        user.setCharm((user.getCharm() == null ? 0 : user.getCharm()) + charm);
        user.setUpdateTime(DateUtil.date());
        update(user);
        return user;
    }

    public User modifyCharm(Integer userId, Integer charm) {
        User user = findById(userId);
        if (user == null) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }

        user.setCharm((user.getCharm() == null ? 0 : user.getCharm()) + charm);
        user.setUpdateTime(DateUtil.date());
        update(user);
        return user;
    }

    @Override
    public User modifyVirtualBalance(Integer userId, Integer price) {
        User user = findById(userId);
        if (user == null) {
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }

        return modifyVirtualBalance(user, price);
    }

    @Override
    public User modifyVirtualBalance(User user, Integer price) {
        user.setVirtualBalance(((user.getVirtualBalance() == null) ? 0 : user.getVirtualBalance()) + price);
        user.setUpdateTime(DateUtil.date());
        update(user);
        return user;
    }

    @Override
    public boolean loginReceiveVirtualMoney(User user) {
        String loginKey = RedisKeyEnum.LOGIN_RECEIVE_VIRTUAL_MONEY.generateKey();
        boolean flag = redisOpenService.hasKey(loginKey);
        if (flag) {
            boolean isReceived = redisOpenService.getBitSet(loginKey, user.getId());
            if (isReceived) {
                return false;
            }
        }

        springThreadPoolExecutor.getAsyncExecutor().execute(new Runnable() {
            @Override
            public void run() {
                modifyVirtualBalance(user, Constant.LOGIN_VIRTUAL_MONEY);
                redisOpenService.bitSet(loginKey, user.getId());

                VirtualDetails details = new VirtualDetails();
                details.setUserId(user.getId());
                details.setSum(user.getVirtualBalance());
                details.setMoney(Constant.LOGIN_VIRTUAL_MONEY);
                details.setType(VirtualDetailsTypeEnum.VIRTUAL_MONEY.getType());
                details.setRemark(VirtualDetailsRemarkEnum.LOGIN_BOUNS.getMsg());
                details.setCreateTime(DateUtil.date());
                virtualDetailsService.create(details);
            }
        });
        return true;
    }

    /**
     * 获取用户信息
     *
     * @param userId 非必传，当未传时查询当前用户信息，否则查询所传递用户信息
     * @return
     */
    @Override
    public UserVO getUserInfo(Integer userId) {
        // 写访问日志
        Integer currentUserId = getCurrentUser().getId();
        int accessCount = 0;
        // 非当前用户才插入访问记录
        if (userId != null && userId > 0 && userId.intValue() != currentUserId.intValue()) {
            // 写访问日志
            AccessLog accessLog = new AccessLog();
            accessLog.setFromUserId(currentUserId);
            accessLog.setToUserId(userId);
            accessLog.setMenusName("首页");
            accessLogService.save(accessLog);
        } else {
            userId = currentUserId;
            String key = RedisKeyEnum.ACCESS_COUNT.generateKey(currentUserId);
            if(redisOpenService.hasKey(key)){
                String accessCountStr = redisOpenService.get(key);
                if(accessCountStr != null && !"".equals(accessCountStr)){
                    accessCount = Integer.parseInt(accessCountStr);
                }
            }
        }
        UserVO userVO = new UserVO();
        User user = findById(userId);
        BeanUtil.copyProperties(user,userVO);
        userVO.setImPsw("");
        // 设置用户扩展信息（兴趣、职业、简介、视频、以及相册）
        setUserExtInfo(userVO, userId);
        //设置用户月收入
        userVO.setMonthIncome(moneyDetailsService.monthIncome(userId));
        //设置用户来访次数
        userVO.setAccessCount(accessCount);
        // 获取新增属性信息
        return userVO;
    }

    /**
     * 设置用户扩展信息
     *
     * @param userVO
     * @param userId
     */
    private void setUserExtInfo(UserVO userVO, Integer userId) {
        UserInfoAuth userInfoAuth = userInfoAuthService.findByUserId(userId);
        if (userInfoAuth != null) {
            userVO.setInterests(userInfoAuth.getInterests());
            userVO.setAbout(userInfoAuth.getAbout());
            userVO.setProfession(userInfoAuth.getProfession());
            List<UserInfoAuthFile> videoFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuth.getId(), FileTypeEnum.VIDEO.getType());
            //设置用户视频
            if (videoFiles != null && !videoFiles.isEmpty()) {
                userVO.setVideoUrl(videoFiles.get(0).getUrl());
            }
            List<UserInfoAuthFile> picFiles = userInfoAuthFileService.findByUserAuthIdAndType(userInfoAuth.getId(), FileTypeEnum.PIC.getType());
            //设置用户相册
            if (picFiles != null && !picFiles.isEmpty()) {
                String[] picArr = new String[picFiles.size()];
                for (int i = 0; i < picFiles.size(); i++) {
                    picArr[i] = picFiles.get(i).getUrl();
                }
                userVO.setPicUrls(picArr);
            }
            List<Product> productList = productService.findByUserId(userId);
            if(productList != null && !productList.isEmpty()){
                UserTechAuth userTechAuth = null;
                for(int i = 0; i < productList.size(); i++){
                    userTechAuth = userTechAuthService.findById(productList.get(i).getTechAuthId());
                    if(userTechAuth != null){
                        productList.get(i).setOrderCount(userTechAuth.getOrderCount());
                        productList.get(i).setVirtualOrderCount(userTechAuth.getVirtualOrderCount());
                        productList.get(i).setVoice(userTechAuth.getVoice());
                        productList.get(i).setVoiceDuration(userTechAuth.getVoiceDuration());
                        productList.get(i).setScoreAvg(userTechAuth.getScoreAvg());
                    }
                }
            }
            userVO.setUserProducts(productList);
        }
        // 为认证用户信息一样可以获取动态相关信息
        List<DynamicVO> newestDynamicList = dynamicService.getNewestDynamicList(userId);
        userVO.setNewestDynamics(newestDynamicList);
        Integer currentUserId = getCurrentUser().getId();
        Integer isAttention = redisOpenService.getBitSet(RedisKeyEnum.ATTENTION_USERS.generateKey(currentUserId),userId) ? 1: 0;
        userVO.setIsAttention(isAttention);
        //获取关注数
        int attentions = redisOpenService.bitCount(RedisKeyEnum.ATTENTION_USERS.generateKey(userId)).intValue();
        //获取粉丝数
        int fans = redisOpenService.bitCount(RedisKeyEnum.ATTENTIONED_USERS.generateKey(userId)).intValue();
        userVO.setAttentions(attentions);
        userVO.setFans(fans);
    }


    @Override
    public UserOnlineVO userOnline(Boolean active, String version) {

        UserOnlineVO uo = new UserOnlineVO();

        User user = this.getCurrentUser();
        UserInfoAuth ua = userInfoAuthService.findByUserId(user.getId());
        if (active) {

            uo.setNeedSayHello(this.getUserRandStatus(user.getId()));

            if (ua != null) {
                uo.setOpenAgentIm(ua.getOpenSubstituteIm());
            }

            log.info("userId:{}用户上线了!;version:{}", user.getId(), version);
            redisOpenService.set(RedisKeyEnum.USER_ONLINE_KEY.generateKey(user.getId()), user.getType() + "");


            if (ua != null && ua.getImSubstituteId() != null) {

                //删除陪玩师的未读信息数量
                Map<String, Object> map = redisOpenService.hget(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(ua.getImSubstituteId().intValue()));

                if (MapUtils.isNotEmpty(map)) {

                    map.remove(user.getImId());

                    if (MapUtils.isEmpty(map)) {
                        redisOpenService.delete(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(ua.getImSubstituteId().intValue()));
                    } else {
                        redisOpenService.hset(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(ua.getImSubstituteId().intValue()), map, Constant.ONE_DAY * 3);
                    }

                }

                //获取代聊天记录
                AdminImLogVO ail = new AdminImLogVO();
                ail.setOwnerUserId(user.getId());
                List<AdminImLog> list = adminImLogService.findByParameter(ail);

                uo.setImLogList(list);

                //删除带聊天记录
                adminImLogService.deleteByOwnerUserId(user.getId());

            }

        } else {
            log.info("userId:{}用户下线了!version:{}", user.getId(), version);
            redisOpenService.delete(RedisKeyEnum.USER_ONLINE_KEY.generateKey(user.getId()));
        }
        return uo;
    }

    @Override
    public Boolean removeUserLoginToken(Integer userId) {
        Set<String> keys = redisOpenService.keys(RedisKeyEnum.PLAY_TOKEN.generateKey() + "*#" + userId);
        log.info("查找到用户对应的token有:{}" + keys);
        if (keys.isEmpty()) {
            return false;
        }
        for (String key : keys) {
            redisOpenService.delete(key);
        }
        return true;
    }


    @Override
    public PageInfo<User> searchByAuthUserInfo(Integer pageNum, Integer pageSize, Integer currentAdminId, String searchword) {
        PageHelper.startPage(pageNum, pageSize);

        List<User> list = userDao.searchByAuthUserInfo(searchword, currentAdminId);

        return new PageInfo<User>(list);
    }


    @Override
    public PageInfo<User> searchByUserInfo(Integer pageNum, Integer pageSize, Integer currentAuthUserId, String searchword) {
        PageHelper.startPage(pageNum, pageSize);

        List<User> list = userDao.searchByUserInfo(searchword, currentAuthUserId);

        return new PageInfo<User>(list);
    }

    @Override
    public boolean getUserRandStatus(Integer userId) {
        String userIdJsonStr = redisOpenService.get(RedisKeyEnum.AUTO_SAY_HELLO_USER_LIST.generateKey());

        if (StringUtils.isNotBlank(userIdJsonStr)) {
            JSONArray userIdArr = com.alibaba.fastjson.JSONObject.parseArray(userIdJsonStr);

            for (int i = 0; i < userIdArr.size(); i++) {
                if (userIdArr.getIntValue(i) == userId.intValue()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public void setUserRandStatus(Integer userId) {
        String userIdJsonStr = redisOpenService.get(RedisKeyEnum.AUTO_SAY_HELLO_USER_LIST.generateKey());

        if (StringUtils.isNotBlank(userIdJsonStr)) {
            JSONArray userIdArr = com.alibaba.fastjson.JSONObject.parseArray(userIdJsonStr);

            for (int i = 0; i < userIdArr.size(); i++) {
                if (userIdArr.getIntValue(i) == userId.intValue()) {
                    userIdArr.remove(i);
                    redisOpenService.set(RedisKeyEnum.AUTO_SAY_HELLO_USER_LIST.generateKey(), userIdArr.toJSONString());
                    return;
                }
            }
        }
    }

    /**
     * 删除用户索引
     */
    @Override
    public void deleteAllUserIndex() {
        userSearchComponent.deleteIndexAll();
    }

    /**
     * 更新用户索引
     */
    @Override
    public void bathUpdateUserIndex() {
        log.info("批量更新所有商品索引");
        List<User> userList = findAllServeUser();
        UserDoc userDoc = new UserDoc();
        for (User user : userList) {
            BeanUtil.copyProperties(user,userDoc);
            userSearchComponent.saveIndex(userDoc);
        }
    }

    /**
     * 获取用户信息
     *
     * @param id
     * @return
     */
    @Override
    public UserVO findUserVOById(Integer id) {
        UserVO userVO = new UserVO();
        User user = findById(id);
        if (user == null) {
            throw new UserException(UserException.ExceptionCode.USER_INFO_NULL_EXCEPTION);
        }
        BeanUtil.copyProperties(user, userVO);
        // 设置用户扩展信息（兴趣、职业、简介、视频、以及相册）
        setUserExtInfo(userVO, id);
        //查询用户所有标签
        List<TagVO> allPersonTagVos = userInfoAuthService.findAllUserTagSelected(id, false);
        userVO.setGroupTags(allPersonTagVos);
        UserVO params = new UserVO();
        params.setUserId(id);
        //设置收入和消费信息
        List<UserVO> list = userDao.findBySearch(params);
        if (CollectionUtil.isNotEmpty(list)) {
            UserVO tmp = list.get(0);
            userVO.setPaySum(tmp.getPaySum());
            userVO.setOrderCount(tmp.getOrderCount());
            userVO.setPayUnitPrice(tmp.getPayUnitPrice());
            userVO.setIncomeSum(tmp.getIncomeSum());
            userVO.setServiceOrderCount(tmp.getServiceOrderCount());
            userVO.setIncomeUnitPrice(tmp.getIncomeUnitPrice());
        } else {
            userVO.setPaySum(new BigDecimal("0.00"));
            userVO.setOrderCount(0);
            userVO.setPayUnitPrice(new BigDecimal("0.00"));
            userVO.setIncomeSum(new BigDecimal("0.00"));
            userVO.setServiceOrderCount(0);
            userVO.setIncomeUnitPrice(new BigDecimal("0.00"));
        }
        userVO.setMainPicUrl("");
        UserInfoAuth userInfoAuth = userInfoAuthService.findByUserId(id);
        if (userInfoAuth != null) {
            userVO.setMainPicUrl(userInfoAuth.getMainPicUrl());
        }
        return userVO;
    }
}