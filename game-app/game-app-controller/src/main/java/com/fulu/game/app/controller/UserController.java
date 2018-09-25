package com.fulu.game.app.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.FileTypeEnum;
import com.fulu.game.common.enums.UserBodyAuthStatusEnum;
import com.fulu.game.common.enums.UserTypeEnum;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.UserBodyAuthVO;
import com.fulu.game.core.entity.vo.UserCommentVO;
import com.fulu.game.core.entity.vo.UserOnlineVO;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.UserTechAuthServiceImpl;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;
    @Qualifier(value = "userInfoAuthServiceImpl")
    @Autowired
    private UserInfoAuthService userInfoAuthService;
    @Autowired
    private OssUtil ossUtil;
    @Autowired
    private ImService imService;
    @Qualifier(value = "userTechAuthServiceImpl")
    @Autowired
    private UserTechAuthServiceImpl userTechAuthService;
    @Autowired
    private AdviceService adviceService;
    @Autowired
    private UserInfoAuthFileService userInfoAuthFileService;
    @Autowired
    private UserCommentService commentService;
    @Autowired
    private UserBodyAuthService userBodyAuthService;
    @Autowired
    private TechTagService techTagService;

    @Autowired
    private UserCommentTagService userCommentTagService;
    @Autowired
    private UserProfessionService userProfessionService;

    @Autowired
    private TagService tagService;

    /**
     * 修改/填写资料
     *
     * @param userVO
     * @return
     */
    @RequestMapping("update")
    public Result update(UserVO userVO) {
        User user = new User();
        if (userVO.getBirth() != null) {
            user.setAge(DateUtil.ageOfNow(userVO.getBirth()));
        }
        user.setId(userService.getCurrentUser().getId());
        user.setGender(userVO.getGender());
        user.setCity(userVO.getCity());
        user.setProvince(userVO.getProvince());
        user.setCountry(userVO.getCountry());
        user.setBirth(userVO.getBirth());
        user.setConstellation(userVO.getConstellation());
        user.setNickname(userVO.getNickname());
        user.setHeadPortraitsUrl(ossUtil.activateOssFile(userVO.getHeadPortraitsUrl()));
        userService.update(user);
        user = userService.findById(userService.getCurrentUser().getId());
        userService.updateRedisUser(user);
        // 保存用户认证信息
        saveUserInfoAuth(userVO);
        user.setIdcard(null);
        user.setRealname(null);
        return Result.success().data(user).msg("个人信息设置成功！");
    }

    /**
     * 获取用户信息
     *
     * @param userId 非必传，当未传时查询当前用户信息，否则查询所传递用户信息
     * @return
     */
    @RequestMapping("get")
    public Result get(@RequestParam(required = false) Integer userId) {
        UserVO userVO = userService.getUserInfo(userId);
        return Result.success().data(userVO).msg("获取个人信息成功！");
    }


    /**
     * 获取用户的基础信息
     *
     * @param userId
     * @return
     */
    @RequestMapping("info")
    public Result getInfo(Integer userId) {
        User user = userService.findById(userId);
        return Result.success().data(user);
    }


    /**
     * 保存用户认证信息
     *
     * @param userVO
     */
    private void saveUserInfoAuth(UserVO userVO) {
        User user = userService.getCurrentUser();
        // 当存在用户认证信息时取修改
        if (userVO != null && (userVO.getInterests() != null ||
                userVO.getProfession() != null || userVO.getAbout() != null ||
                userVO.getPicUrls() != null || userVO.getVideoUrl() != null)) {
            UserInfoAuth userInfoAuth = new UserInfoAuth();
            userInfoAuth.setUserId(user.getId());
            if (userVO.getInterests() != null) {
                userInfoAuth.setInterests(userVO.getInterests());
            }
            if (userVO.getProfession() != null) {
                userInfoAuth.setProfession(userVO.getProfession());
            }
            if (userVO.getAbout() != null) {
                userInfoAuth.setAbout(userVO.getAbout());
            }
            // 判断认证信息是否存在，不存在就新增
            UserInfoAuth tmp = userInfoAuthService.findByUserId(user.getId());
            userInfoAuth.setUpdateTime(new Date());
            if (tmp == null) {
                userInfoAuth.setCreateTime(new Date());
                userInfoAuthService.create(userInfoAuth);
            } else {
                userInfoAuthService.updateByUserId(userInfoAuth);
            }
            saveUserInfoAuthFile(userVO);
        }
    }

    /**
     * 保存用户认证文件信息（相册和视频）
     *
     * @param userVO
     */
    private void saveUserInfoAuthFile(UserVO userVO) {
        User user = userService.getCurrentUser();
        UserInfoAuth userInfoAuth = userInfoAuthService.findByUserId(user.getId());
        if (userInfoAuth != null) {
            // 先删除所有以前图片，然后插入
            userInfoAuthFileService.deleteByUserAuthIdAndType(userInfoAuth.getId(), FileTypeEnum.PIC.getType());
            userInfoAuthFileService.deleteByUserAuthIdAndType(userInfoAuth.getId(), FileTypeEnum.VIDEO.getType());
            if (userVO != null) {
                String[] picUrls = userVO.getPicUrls();
                if (picUrls != null && picUrls.length > 0) {
                    for (int i = 0; i < picUrls.length; i++) {
                        UserInfoAuthFile userInfoAuthFile = new UserInfoAuthFile();
                        userInfoAuthFile.setUrl(picUrls[i]);
                        userInfoAuthFile.setInfoAuthId(userInfoAuth.getId());
                        userInfoAuthFile.setType(FileTypeEnum.PIC.getType());
                        userInfoAuthFile.setName("相册" + (i + 1));
                        userInfoAuthFile.setCreateTime(new Date());
                        userInfoAuthFileService.create(userInfoAuthFile);
                    }
                }
                if (userVO != null && userVO.getVideoUrl() != null) {
                    UserInfoAuthFile userInfoAuthFile = new UserInfoAuthFile();
                    userInfoAuthFile.setUrl(userVO.getVideoUrl());
                    userInfoAuthFile.setInfoAuthId(userInfoAuth.getId());
                    userInfoAuthFile.setType(FileTypeEnum.VIDEO.getType());
                    userInfoAuthFile.setName("视频");
                    userInfoAuthFile.setCreateTime(new Date());
                    userInfoAuthFileService.create(userInfoAuthFile);
                }
            }
        }
    }


    @PostMapping(value = "online")
    public Result userOnline(@RequestParam(required = true) Boolean active, String version) {

        UserOnlineVO uo = userService.userOnline(active, version);

        return Result.success().data(uo).msg("查询成功！");
    }


    @PostMapping("/third-account/save")
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
        if (status.equals(200)) {
            user.setImId(imId);
            user.setImPsw(imPsw);
            user.setUpdateTime(new Date());
            userService.update(user);
            userService.updateRedisUser(user);
            log.info("用户:{}绑定IM信息成功:user:{};", user.getId(), user);
        } else if (status.equals(500)) {
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
     * 获取用户接单技能列表
     *
     * @return
     */
    @RequestMapping("tech/list")
    public Result userTechList() {
        User user = userService.getCurrentUser();
        //查询所有用户认证的技能
        List<UserTechAuth> techAuthList = userTechAuthService.findUserNormalTechs(user.getId());
        return Result.success().data(techAuthList);
    }

    /**
     * 意见反馈接口
     */
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
        data.put("virtualBalance", user.getVirtualBalance() == null ? 0 : user.getVirtualBalance());
        Integer totalCharm = user.getCharm() == null ? 0 : user.getCharm();
        Integer charmDrawSum = user.getCharmDrawSum() == null ? 0 : user.getCharmDrawSum();
        Integer leftCharm = totalCharm - charmDrawSum;
        data.put("charm", leftCharm);
        data.put("charmMoney", new BigDecimal(leftCharm)
                .multiply(Constant.CHARM_TO_MONEY_RATE).setScale(2, BigDecimal.ROUND_HALF_DOWN));
        data.put("chargeBalance", user.getChargeBalance());
        return Result.success().data(data).msg("查询成功！");
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
     * 查询-用户-列表
     * 只查陪玩师
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping("/list")
    public Result list(UserVO userVO,
                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        //只查陪玩师
        userVO.setType(UserTypeEnum.ACCOMPANY_PLAYER.getType());
        PageInfo<UserVO> userList = userService.list(userVO, pageNum, pageSize);
        return Result.success().data(userList).msg("查询用户列表成功！");
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
     * 查询陪玩师评论标签列表接口
     *
     * @param pageNum
     * @param pageSize
     * @param serverId
     * @return
     */
    @RequestMapping(value = "/comment-tag/list")
    public Result findCommentsTagList(Integer pageNum,
                                      Integer pageSize,
                                      Integer serverId) {
        PageInfo<UserCommentTag> page = userCommentTagService.findByServerId(pageNum, pageSize, serverId);
        return Result.success().data(page);
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

    /**
     * 用户-获取用户认证信息
     *
     * @return
     */
    @PostMapping("/body-auth/get")
    public Result getUserAuthInfo() {
        User user = userService.getCurrentUser();
        UserBodyAuth authInfo = userBodyAuthService.getUserAuthInfo(user.getId());
        return Result.success().data(authInfo).msg("查询成功！");
    }


    /**
     * 陪玩师技能标签列表
     *
     * @return
     */
    @PostMapping("/tech/tags")
    public Result getUserTechTags(@RequestParam(required = true) Integer userId,
                                  @RequestParam(required = true) Integer categoryId) {
        UserTechAuth userTechAuth = userTechAuthService.findTechByCategoryAndUser(categoryId, userId);
        List<TechTag> techTags = new ArrayList<>();
        if (userTechAuth != null) {
            techTags = techTagService.findByTechAuthId(userTechAuth.getId());
        }
        return Result.success().data(techTags);
    }


    /**
     * 陪玩师添加自己的技能标签
     *
     * @param categoryId
     * @return
     */
    @PostMapping("/tech/tag/add")
    public Result getUserTechTagAdd(@RequestParam(required = true) String tagName,
                                    @RequestParam(required = true) Integer categoryId) {
        User user = userService.getCurrentUser();
        Tag tag = tagService.createUserCustomTag(user.getId(), categoryId, tagName);
        return Result.success().data(tag).msg("创建自定义标签成功!");
    }


    /**
     * 陪玩师添加自己的技能标签
     *
     * @return
     */
    @PostMapping("/tech/tag/del")
    public Result getUserTechTagAdd(@RequestParam(required = true) Integer tagId) {
        User user = userService.getCurrentUser();
        tagService.delUserTag(user.getId(), tagId);
        return Result.success().msg("删除标签成功!!");
    }


    /**
     * 获取用户职业列表
     * @return
     */
    @RequestMapping("/user-profession/all-list")
    public Result userProfessionList() {
        List<UserProfession> userProfessions = userProfessionService.findUserProfessionList();
        return Result.success().data(userProfessions);
    }

}
