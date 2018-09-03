package com.fulu.game.app.controller;

import cn.hutool.core.date.DateUtil;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.RedisKeyEnum;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.AdminImLogVO;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.RedisOpenServiceImpl;
import com.fulu.game.core.service.impl.UserTechAuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private RedisOpenServiceImpl redisOpenService;
    @Autowired
    private AdminImLogService adminImLogService;
    @Autowired
    private ImService imService;
    @Qualifier(value = "userTechAuthServiceImpl")
    @Autowired
    private UserTechAuthServiceImpl userTechAuthService;
    @Autowired
    private AdviceService adviceService;
    @Autowired
    private UserInfoAuthFileService userInfoAuthFileService;


    /**
     * 修改/填写资料
     *
     * @param userVO
     * @return
     */
    @RequestMapping("update")
    public Result update(UserVO userVO) {
        User user = userService.findById(userService.getCurrentUser().getId());
        user.setAge(DateUtil.ageOfNow(userVO.getBirth()));
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
        // 保存用户认证信息
        saveUserInfoAuth(userVO);
        user.setIdcard(null);
        user.setRealname(null);
        return Result.success().data(user).msg("个人信息设置成功！");
    }

    /**
     * 获取用户信息
     * @param userId 非必传，当未传时查询当前用户信息，否则查询所传递用户信息
     * @return
     */
    @RequestMapping("get")
    public Result get(@RequestParam(required = false) Integer userId) {
        UserVO userVO = userService.getUserInfo(userId);
        return Result.success().data(userVO).msg("获取个人信息成功！");
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
                userVO.getProfession() != null || userVO.getAbout() != null)) {
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
            if (tmp == null) {
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
            userInfoAuthFileService.deleteByUserAuthIdAndType(userInfoAuth.getId(), 1);
            userInfoAuthFileService.deleteByUserAuthIdAndType(userInfoAuth.getId(), 3);
            if (userVO != null && (userVO.getPicUrls() != null || userVO.getVideoUrl() != null)) {
                String[] picUrls = userVO.getPicUrls();
                if (picUrls != null && picUrls.length > 0) {
                    for (int i = 0; i < picUrls.length; i++) {
                        UserInfoAuthFile userInfoAuthFile = new UserInfoAuthFile();
                        userInfoAuthFile.setUrl(picUrls[i]);
                        userInfoAuthFile.setInfoAuthId(userInfoAuth.getId());
                        userInfoAuthFile.setType(1);
                        userInfoAuthFile.setName("相册" + 1);
                        userInfoAuthFile.setCreateTime(new Date());
                        userInfoAuthFileService.create(userInfoAuthFile);
                    }
                }
                if (userVO != null && userVO.getVideoUrl() != null) {
                    UserInfoAuthFile userInfoAuthFile = new UserInfoAuthFile();
                    userInfoAuthFile.setUrl(userVO.getVideoUrl());
                    userInfoAuthFile.setInfoAuthId(userInfoAuth.getId());
                    userInfoAuthFile.setType(3);
                    userInfoAuthFile.setName("视频");
                    userInfoAuthFile.setCreateTime(new Date());
                    userInfoAuthFileService.create(userInfoAuthFile);
                }
            }
        }
    }


    @PostMapping(value = "online")
    public Result userOnline(@RequestParam(required = true) Boolean active, String version) {
        User user = userService.getCurrentUser();
        UserInfoAuth ua = userInfoAuthService.findByUserId(user.getId());
        if (active) {
            log.info("userId:{}用户上线了!;version:{}", user.getId(), version);
            redisOpenService.set(RedisKeyEnum.USER_ONLINE_KEY.generateKey(user.getId()), user.getType() + "");

            if(ua!=null && ua.getImSubstituteId()!=null){


                //删除陪玩师的未读信息数量
                Map map = redisOpenService.hget(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(ua.getImSubstituteId().intValue()));

                if(map != null && map.size() >0 ){

                    map.remove(user.getImId());

                    if(map.size() <1){
                        redisOpenService.delete(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(ua.getImSubstituteId().intValue()));
                    }else{
                        redisOpenService.hset(RedisKeyEnum.IM_COMPANY_UNREAD.generateKey(ua.getImSubstituteId().intValue()) , map , Constant.ONE_DAY * 3);
                    }

                }

                //获取代聊天记录
                AdminImLogVO ail = new AdminImLogVO();
                ail.setOwnerUserId(user.getId());
                List<AdminImLog> list = adminImLogService.findByParameter(ail);
                //删除带聊天记录
                adminImLogService.deleteByOwnerUserId(user.getId());
                return Result.success().data(list).msg("查询成功！");

            }

            return Result.success().msg("查询成功！");
        } else {
            log.info("userId:{}用户下线了!version:{}", user.getId(), version);
            redisOpenService.delete(RedisKeyEnum.USER_ONLINE_KEY.generateKey(user.getId()));
        }
        return Result.success();
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
     * （陪玩师）魅力值提现
     *
     * @param userId 陪玩师id
     * @param charm  魅力值
     * @return 封装结果集
     */
    @PostMapping("/charm/withdraw")
    public Result withdrawCharm(@RequestParam Integer userId, @RequestParam Integer charm) {
        userInfoAuthService.withdrawCharm(userId, charm);
        return Result.success().msg("提现成功");
    }
}
