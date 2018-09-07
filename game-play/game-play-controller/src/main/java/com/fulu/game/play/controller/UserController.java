package com.fulu.game.play.controller;

import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.config.WxMaServiceSupply;
import com.fulu.game.common.enums.PlatformEcoEnum;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.enums.UserBodyAuthStatusEnum;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.threadpool.SpringThreadPoolExecutor;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.common.utils.SMSUtil;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.core.service.impl.UserTechAuthServiceImpl;
import com.fulu.game.play.service.impl.PlayCouponOpenServiceImpl;
import com.fulu.game.play.utils.RequestUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {

    @Qualifier(value = "userTechAuthServiceImpl")
    @Autowired
    private UserTechAuthServiceImpl userTechAuthService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserInfoAuthService userInfoAuthService;
    @Autowired
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private UserCommentService commentService;
    @Autowired
    private WxMaServiceSupply wxMaServiceSupply;
    @Autowired
    private OssUtil ossUtil;
    @Autowired
    private ProductService productService;
    @Autowired
    private ImService imService;
    @Autowired
    private AdviceService adviceService;
    @Autowired
    private PlayCouponOpenServiceImpl playCouponOpenServiceImpl;
    @Autowired
    private SpringThreadPoolExecutor springThreadPoolExecutor;
    @Autowired
    private UserBodyAuthService userBodyAuthService;

    @RequestMapping("tech/list")
    public Result userTechList() {
        User user = userService.getCurrentUser();
        //查询所有用户认证的技能
        List<UserTechAuth> techAuthList = userTechAuthService.findUserNormalTechs(user.getId());
        return Result.success().data(techAuthList);
    }

    /**
     * 用户-查询余额
     * 账户金额不能从缓存取，因为存在管理员给用户加零钱缓存并未更新
     *
     * @return
     */
    @PostMapping("/balance/get")
    public Result getBalance() {
        User user = userService.findById(userService.getCurrentUser().getId());
        JSONObject data = new JSONObject();
        data.put("balance", user.getBalance());
        data.put("virtualBalance", user.getVirtualBalance());
        data.put("charm", user.getCharm());
        return Result.success().data(data).msg("查询成功！");
    }

    /**
     * 用户-进入我的页面
     *
     * @return
     */
    @PostMapping("/get")
    public Result get() {
        User user = userService.findById(userService.getCurrentUser().getId());
        return Result.success().data(user).msg("查询信息成功！");
    }

    /**
     * 用户-更新个人信息
     *
     * @param userVO
     * @return
     */
    @RequestMapping("/update")
    public Result update(UserVO userVO) {
        User user = userService.findById(userService.getCurrentUser().getId());
        user.setAge(userVO.getAge());
        user.setGender(userVO.getGender());
        user.setCity(userVO.getCity());
        user.setProvince(userVO.getProvince());
        user.setCountry(userVO.getCountry());
        user.setBirth(userVO.getBirth());
        user.setConstellation(userVO.getConstellation());
        user.setNickname(userVO.getNickname());
        user.setHeadPortraitsUrl(ossUtil.activateOssFile(userVO.getHeadPortraitsUrl()));
        userService.update(user);
        userService.updateRedisUser(user);
        user.setIdcard(null);
        user.setRealname(null);
        return Result.success().data(user).msg("个人信息设置成功！");
    }


    /**
     * 点击发送验证码接口
     *
     * @param mobile
     * @return
     */
    @PostMapping("/mobile/sms")
    public Result sms(@RequestParam("mobile") String mobile) {
        String token = SubjectUtil.getToken();
        //缓存中查找该手机是否有验证码
        if (redisOpenService.hasKey(RedisKeyEnum.SMS.generateKey(mobile))) {
            String times = redisOpenService.get(RedisKeyEnum.SMS.generateKey(mobile));
            if (Integer.parseInt(times) > Constant.MOBILE_CODE_SEND_TIMES) {
                return Result.error().msg("半小时内发送次数不能超过" + Constant.MOBILE_CODE_SEND_TIMES + "次，请等待！");
            } else {
                String verifyCode = SMSUtil.sendVerificationCode(mobile);
                log.info("发送验证码{}={}", mobile, verifyCode);
                redisOpenService.hset(RedisKeyEnum.SMS.generateKey(token), mobile, verifyCode, Constant.VERIFYCODE_CACHE_TIME);
                times = String.valueOf(Integer.parseInt(times) + 1);
                redisOpenService.set(RedisKeyEnum.SMS.generateKey(mobile), times, Constant.MOBILE_CACHE_TIME);
                return Result.success().msg("验证码发送成功！");
            }
        } else {
            String verifyCode = SMSUtil.sendVerificationCode(mobile);
            log.info("发送验证码{}={}", mobile, verifyCode);
            redisOpenService.hset(RedisKeyEnum.SMS.generateKey(token), mobile, verifyCode, Constant.VERIFYCODE_CACHE_TIME);
            redisOpenService.set(RedisKeyEnum.SMS.generateKey(mobile), "1", Constant.MOBILE_CACHE_TIME);
            return Result.success().msg("验证码发送成功！");
        }
    }


    /**
     * 获取用户微信手机号
     *
     * @param encryptedData
     * @param iv
     * @return
     */
    @PostMapping("/wxinfo/mobile")
    public Result getWinfoMobile(String encryptedData,
                                 String iv) {

        WxMaPhoneNumberInfo phoneNoInfo = null;
        String sessionKey = redisOpenService.get(RedisKeyEnum.WX_SESSION_KEY.generateKey(SubjectUtil.getToken()));
        try {
            phoneNoInfo = wxMaServiceSupply.playWxMaService().getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);
        } catch (Exception e) {
            log.error("获取用户微信异常:encryptedData:{};iv:{};sessionKey:{};{}", encryptedData, iv, sessionKey, e.getMessage());
            throw new UserException(UserException.ExceptionCode.SESSION_KEY_DISABLE_EXCEPTION);
        }
        if (phoneNoInfo == null || phoneNoInfo.getPurePhoneNumber() == null) {
            log.error("未获取用户微信手机号:phoneNoInfo:{};encryptedData:{};iv:{};sessionKey:{};", phoneNoInfo, encryptedData, iv, sessionKey);
            throw new UserException(UserException.ExceptionCode.WX_PHONE_NOT_EXIST_EXCEPTION);
        }
        User cacheUser = userService.getCurrentUser();
        User user = new User();
        user.setId(cacheUser.getId());
        log.info("获取用户微信手机号,原用户信息:{}", user);
        user.setMobile(phoneNoInfo.getPurePhoneNumber());
        user.setUpdateTime(new Date());
        try {
            userService.update(user);
            BeanUtil.copyProperties(user, cacheUser, CopyOptions.create().setIgnoreNullValue(true));
            userService.updateRedisUser(cacheUser);
        } catch (Exception e) {
            log.error("更新微信手机号错误用户信息:{};手机号:{};", user, user.getMobile());
            return Result.error().msg("手机号已被注册!");
        }
        log.info("获取用户微信手机号,更新后用户信息:{};手机号:{};", user, user.getMobile());
        return Result.success().data(cacheUser);

    }


    /**
     * 保存微信信息
     *
     * @param wxUserInfo 微信用户信息
     * @return 封装结果集
     */
    @PostMapping("/wxinfo/save")
    public Result saveWxUserInfo(WxUserInfo wxUserInfo, HttpServletRequest request) {
        User tempUser = userService.getCurrentUser();
        UserVO user = new UserVO();
        BeanUtil.copyProperties(tempUser, user);
        WxMaUserInfo wxMaUserInfo = null;
        try {
            String sessionKey = redisOpenService.get(RedisKeyEnum.WX_SESSION_KEY.generateKey(SubjectUtil.getToken()));
            if (wxUserInfo.getEncryptedData() != null && wxUserInfo.getIv() != null) {
                wxMaUserInfo = wxMaServiceSupply.playWxMaService().getUserService().getUserInfo(sessionKey, wxUserInfo.getEncryptedData(), wxUserInfo.getIv());
            }
        } catch (Exception e) {
            log.error("获取用户微信异常:wxUserInfo:{};{}", wxUserInfo, e.getMessage());
        }
        if (wxMaUserInfo != null) {
            user.setUnionId(wxMaUserInfo.getUnionId());
        }
        if (user.getGender() == null) {
            user.setGender(wxUserInfo.getGender());
        }
        if (user.getNickname() == null) {
            user.setNickname(wxUserInfo.getNickName());
        }
        if (user.getHeadPortraitsUrl() == null) {
            user.setHeadPortraitsUrl(wxUserInfo.getAvatarUrl());
        }
        if (user.getCity() == null) {
            user.setCity(wxUserInfo.getCity());
        }
        if (user.getProvince() == null) {
            user.setProvince(wxUserInfo.getProvince());
        }
        if (user.getCountry() == null) {
            user.setCountry(wxUserInfo.getCountry());
        }
        user.setUpdateTime(new Date());
        String ipStr = RequestUtil.getIpAdrress(request);
        User resultUser = null;
        if (user.getUnionId() == null) {
            userService.update(user);
            userService.updateRedisUser(user);
            resultUser = user;
        } else {
            resultUser = userService.updateUnionUser(user, PlatformEcoEnum.PLAY, ipStr);
            if (Constant.SEND_COUPOU_SUCCESS.equals(user.getCoupouStatus())) {
                //新线程发放优惠券（避免事务问题：当前方法的事务和优惠券发放的事务，因为都涉及到t_user表的操作，可能造成数据库死锁）
                springThreadPoolExecutor.getAsyncExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        playCouponOpenServiceImpl.generateCoupon(Constant.NEW_POINT_USER_COUPON_GROUP_REDEEM_CODE, user.getId(), DateUtil.date(), ipStr);
                    }
                });
            }
        }
        return Result.success().data(resultUser);
    }

    /**
     * 绑定手机号
     *
     * @param mobile
     * @param verifyCode
     * @return
     */
    @PostMapping("/mobile/bind/new")
    public Result mobileBind(String mobile,
                             String verifyCode) {
        String token = SubjectUtil.getToken();
        //验证手机号的验证码
        String redisVerifyCode = redisOpenService.hget(RedisKeyEnum.SMS.generateKey(token), mobile);
        if (null == redisVerifyCode) {
            return Result.error().msg("验证码失效");
        }
        if (verifyCode != null && !verifyCode.equals(redisVerifyCode)) {
            return Result.error().msg("验证码提交错误");
        }
        User currentUser = userService.getCurrentUser();
        User openIdUser = userService.findByOpenId(currentUser.getOpenId(), PlatformEcoEnum.PLAY);
        //如果openId已经绑定手机号
        if (openIdUser != null && openIdUser.getMobile() != null) {
            return Result.error().msg("已经绑定过手机号！");
        }
        User newUser;
        User mobileUser = userService.findByMobile(mobile);
        if (mobileUser != null) {
            if (mobileUser.getOpenId() != null) {
                return Result.error().msg("该手机号已经被绑定！");
            } else {
                mobileUser.setRegistIp(currentUser.getRegistIp());

                mobileUser.setOpenId(currentUser.getOpenId());
                mobileUser.setUpdateTime(new Date());
                userService.update(mobileUser);
                userService.deleteById(openIdUser.getId());
                newUser = mobileUser;
            }
        } else {
            openIdUser.setMobile(mobile);
            openIdUser.setUpdateTime(new Date());
            userService.update(openIdUser);
            newUser = openIdUser;
            userService.updateRedisUser(newUser);
        }
        return Result.success().data(newUser).msg("手机号绑定成功！");
    }


    /**
     * 绑定手机号且更新用户信息
     *
     * @param wxUserInfo
     * @return
     */
    @PostMapping("/mobile/bind")
    public Result bind(WxUserInfo wxUserInfo) {
        String token = SubjectUtil.getToken();
        //验证手机号的验证码
        String redisVerifyCode = redisOpenService.hget(RedisKeyEnum.SMS.generateKey(token), wxUserInfo.getMobile());
        if (null == redisVerifyCode) {
            return Result.error().msg("验证码失效");
        }
        String verifyCode = wxUserInfo.getVerifyCode();
        if (verifyCode != null && !verifyCode.equals(redisVerifyCode)) {
            return Result.error().msg("验证码提交错误");
        } //绑定手机号
        User user = userService.getCurrentUser();
        String openId = user.getOpenId();
        User newUser = null;
        User openIdUser = userService.findByOpenId(openId, PlatformEcoEnum.PLAY);
        //如果openId已经绑定手机号
        if (openIdUser != null && openIdUser.getMobile() != null) {
            return Result.error().msg("已经绑定过手机号！");
        }
        User mobileUser = userService.findByMobile(wxUserInfo.getMobile());
        if (mobileUser != null) {
            if (mobileUser.getOpenId() != null) {
                return Result.error().msg("该手机号已经被绑定！");
            } else {
                mobileUser.setOpenId(openId);
                mobileUser.setGender(wxUserInfo.getGender());
                mobileUser.setNickname(wxUserInfo.getNickName());
                mobileUser.setHeadPortraitsUrl(wxUserInfo.getAvatarUrl());
                mobileUser.setCity(wxUserInfo.getCity());
                mobileUser.setProvince(wxUserInfo.getProvince());
                mobileUser.setCountry(wxUserInfo.getCountry());
                mobileUser.setUpdateTime(new Date());
                userService.update(mobileUser);
                userService.deleteById(openIdUser.getId());
                newUser = mobileUser;
            }
        } else {
            openIdUser.setMobile(wxUserInfo.getMobile());
            openIdUser.setGender(wxUserInfo.getGender());
            openIdUser.setNickname(wxUserInfo.getNickName());
            openIdUser.setHeadPortraitsUrl(wxUserInfo.getAvatarUrl());
            openIdUser.setCity(wxUserInfo.getCity());
            openIdUser.setProvince(wxUserInfo.getProvince());
            openIdUser.setCountry(wxUserInfo.getCountry());
            openIdUser.setUpdateTime(new Date());
            userService.update(openIdUser);
            newUser = openIdUser;
        }
        userService.updateRedisUser(newUser);
        return Result.success().data(newUser).msg("手机号绑定成功！");
    }


    @PostMapping("/im/save")
    public Result imSave(@RequestParam("status") Integer status,
                         @RequestParam("imId") String imId,
                         @RequestParam("imPsw") String imPsw,
                         @RequestParam(value = "errorMsg", required = false) String errorMsg) {
        User user = userService.findById(userService.getCurrentUser().getId());
        if (null == user) {
            log.info("当前用户id={}查询数据库不存在，无法绑定", userService.getCurrentUser().getId());
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }
        log.info("IM注册请求开始,请求参数status:{},user:{},imId={},imPsw={},errorMsg={}", user, status, imId, imPsw, errorMsg);
        if (user.getImId() != null) {
            log.info("用户IM信息已经存在:user:{};", user);
            return Result.success().data(user).msg("已存在IM账号");
        }
        if (status == 200) {
            user.setImId(imId);
            user.setImPsw(imPsw);
            user.setUpdateTime(new Date());
            userService.update(user);
            userService.updateRedisUser(user);
            log.info("用户:{}绑定IM信息成功:user:{};", user.getId(), user);
        } else if (status == 500) {
            String newIMId = "s" + imId;
            ImUser imUser = imService.registerUser(newIMId, imPsw);
            user.setImId(imUser.getUsername());
            user.setImPsw(imUser.getImPsw());
            user.setUpdateTime(new Date());
            userService.update(user);
            userService.updateRedisUser(user);
            log.error("用户:{}绑定IM失败,失败原因:{}", user, errorMsg);
        }
        return Result.success().data(user).msg("IM用户信息保存成功！");
    }

    /**
     * 用户-根据imId查询聊天对象User
     *
     * @return
     */
    @PostMapping("/im/get")
    public Result getImUser(@RequestParam("imId") String imId,
                            String content) {
        log.info("根据imId获取用户信息:imId:{};content:{}", imId, content);
        if (StringUtils.isEmpty(imId)) {
            throw new UserException(UserException.ExceptionCode.IllEGAL_IMID_EXCEPTION);
        }
        User user = userService.findByImId(imId);
        if (null == user) {
            return Result.error().msg("未查询到该用户或尚未注册IM");
        }
        log.info("根据imId获取用户信息:imId:{};content:{};user:{}", imId, content, user);
        return Result.success().data(user).msg("查询IM用户成功");
    }

    /**
     * 用户-根据imIds批量查询聊天对象UserList
     *
     * @return
     */
    @PostMapping("/im/list")
    public Result getImUserList(@RequestParam("imIds") String imIds) {
        List<User> userList = userService.findByImIds(imIds);
        return Result.success().data(userList).msg("查询IM用户成功！");
    }

    /**
     * 聊天对象信息获取
     *
     * @param id
     * @return
     */
    @PostMapping("/chatwith/get")
    public Result chatWithGet(@RequestParam("id") Integer id) {
        UserInfoVO userInfoVO = userInfoAuthService.findUserCardByUserId(id, false, true, true, true);
        List<Product> productList = productService.findByUserId(id);
        userInfoVO.setProductList(productList);
        return Result.success().data(userInfoVO).msg("查询聊天对象信息成功！");
    }

    /**
     * 用户-添加评价
     *
     * @return
     */
    @RequestMapping("/comment/save")
    public Result save(UserCommentVO commentVO) {
        commentService.save(commentVO);
        return Result.success().msg("添加成功！");
    }

    /**
     * 用户-查询评论
     *
     * @return
     */
    @RequestMapping("/comment/get")
    public Result get(@RequestParam("orderNo") String orderNo) {
        UserComment comment = commentService.findByOrderNo(orderNo);
        if (null == comment) {
            return Result.error().msg("该评论不存在！");
        }
        comment.setServerUserId(null);
        comment.setScoreAvg(null);
        return Result.success().data(comment).msg("查询成功！");
    }


    /**
     * 查询陪玩师的所有评论
     *
     * @param pageNum
     * @param pageSize
     * @param serverId
     * @return
     */
    @RequestMapping(value = "/comment/byserver")
    public Result findDetailsComments(Integer pageNum,
                                      Integer pageSize,
                                      Integer serverId) {
        PageInfo<UserCommentVO> page = commentService.findByServerId(pageNum, pageSize, serverId);
        return Result.success().data(page);
    }

    /**
     * 技能认证-分享
     *
     * @return
     */
    @RequestMapping("/tech-auth/share")
    public Result getShareCard(@RequestParam("techAuthId") Integer techAuthId,
                               @RequestParam("scene") String scene) throws WxErrorException, IOException {
        log.info("调用技能认证-分享接口，入参:scene= {}，techAuthId={}", scene, techAuthId);
        String techAuthUrl = userService.getTechAuthCard(techAuthId, scene);
        return Result.success().data("techAuthUrl", techAuthUrl);
    }

    /**
     * 技能认证-分享-落地页
     *
     * @param techAuthId
     * @return
     */
    @RequestMapping("/tech-auth/page/get")
    public Result getSharePage(@RequestParam("techAuthId") Integer techAuthId) {
        log.info("调用技能认证-分享落地页接口，入参:techAuthId={}", techAuthId);
        UserInfoVO userInfoVO = userInfoAuthService.getSharePage(techAuthId);
        return Result.success().data(userInfoVO);
    }

    /**
     * 陪玩师名片-分享
     *
     * @return
     */
    @RequestMapping("/tech-card/share")
    public Result getShareCard(@RequestParam("scene") String scene,
                               @RequestParam("productId") Integer productId) throws WxErrorException, IOException {
        log.info("调用/tech-card/share接口，入参:scene= {},productId= {}", scene, productId);
        String techCardUrl = userService.getTechShareCard(scene, productId);
        return Result.success().data("techCardUrl", techCardUrl);
    }

    @PostMapping("/advice/add")
    public Result addAdvice(@RequestParam("content") String content,
                            @RequestParam(value = "contact", required = false) String contact,
                            @RequestParam(value = "advicePicUrls", required = false) String[] advicePicUrls) {
        if (content == null) {
            return Result.error().msg("请填写建议内容");
        }
        Advice advice = adviceService.addAdvice(content, contact, advicePicUrls);
        return Result.success().data(advice).msg("提交成功");
    }

    /**
     * 用户-查询虚拟零钱余额
     *
     * @return
     */
    @PostMapping("/virtual-balance/get")
    public Result getVirtualBalance() {
        User user = userService.findById(userService.getCurrentUser().getId());
        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("virtualBalance", user.getVirtualBalance() == null ? 0 : user.getVirtualBalance());
        return Result.success().data(resultMap).msg("查询成功！");
    }


    /**
     * 用户-获取用户认证信息
     *
     * @return
     */
    @PostMapping("/auth-status/get")
    public Result getUserAuthStatus() {
        User user = userService.getCurrentUser();

        UserBodyAuthVO uba = new UserBodyAuthVO();
        uba.setUserId(user.getId());
        List<UserBodyAuth> list = userBodyAuthService.findByParameter(uba);

        UserBodyAuth authInfo = null;

        if (list != null && list.size() > 0) {
            authInfo = list.get(0);
        }

        return Result.success().data(authInfo).msg("查询成功！");
    }


    /**
     * 用户-提交用户认证信息
     *
     * @return
     */
    @PostMapping("/body-auth/save")
    public Result getUserAuthStatus(String userName, String cardNo, String cardUrl, String cardHandUrl) {
        User user = userService.getCurrentUser();
        UserBodyAuthVO uba = new UserBodyAuthVO();
        uba.setUserId(user.getId());
        uba.setUserName(userName);
        uba.setCardNo(cardNo);
        uba.setCardUrl(ossUtil.activateOssFile(cardUrl));
        uba.setCardHandUrl(ossUtil.activateOssFile(cardHandUrl));
        uba.setAuthStatus(UserBodyAuthStatusEnum.NO_AUTH.getType());
        uba.setCreateTime(new Date());

        userBodyAuthService.submitUserBodyAuthInfo(uba);

        return Result.success().msg("提交成功！");
    }

}
