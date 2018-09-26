package com.fulu.game.h5.controller;

import com.alibaba.fastjson.JSONObject;
import com.fulu.game.common.Constant;
import com.fulu.game.common.Result;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.core.entity.Advice;
import com.fulu.game.core.entity.ImUser;
import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.UserCommentVO;
import com.fulu.game.core.entity.vo.UserInfoVO;
import com.fulu.game.core.entity.vo.UserVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.impl.UserInfoAuthServiceImpl;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 用户Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/13 17:15
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {
    private final UserCommentService commentService;

    private AdviceService adviceService;

    private UserService userService;

    private OssUtil ossUtil;

    private ImService imService;

    private UserInfoAuthServiceImpl userInfoAuthService;

    private ProductService productService;

    private final UserBodyAuthService userBodyAuthService;

    @Autowired
    public UserController(UserCommentService commentService,
                          AdviceService adviceService,
                          UserService userService,
                          OssUtil ossUtil,
                          ImService imService,
                          UserInfoAuthServiceImpl userInfoAuthService,
                          ProductService productService, UserBodyAuthService userBodyAuthService) {
        this.commentService = commentService;
        this.adviceService = adviceService;
        this.userService = userService;
        this.ossUtil = ossUtil;
        this.imService = imService;
        this.userInfoAuthService = userInfoAuthService;
        this.productService = productService;
        this.userBodyAuthService = userBodyAuthService;
    }

    /**
     * 查询陪玩师的所有评论
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数据条数
     * @param serverId 陪玩师id
     * @return 封装结果集
     */
    @RequestMapping(value = "/comment/byserver")
    public Result findDetailsComments(Integer pageNum,
                                      Integer pageSize,
                                      Integer serverId) {
        PageInfo<UserCommentVO> page = commentService.findByServerId(pageNum, pageSize, serverId);
        return Result.success().data(page);
    }


    /**
     * 提交建议
     *
     * @param content       建议内容
     * @param contact       联系方式【电话或者QQ】
     * @param advicePicUrls 建议截图
     * @return
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
     * 检测用户是否通过实名认证
     *
     * @return 封装结果集
     */
    @PostMapping("/body-auth/check")
    public Result isUserBodyAuth() {
        User user = userService.findById(userService.getCurrentUser().getId());
        if (user == null) {
            log.info("当前用户id={}查询数据库不存在", userService.getCurrentUser().getId());
            throw new UserException(UserException.ExceptionCode.USER_NOT_EXIST_EXCEPTION);
        }

        boolean result = userBodyAuthService.userAlreadyAuth(user.getId());
        if (result) {
            return Result.success().msg("已通过认证！");
        }
        return Result.error().msg("未通过认证！");
    }

    /**
     * 获取用户钱包
     *
     * @return 封装结果集
     */
    @PostMapping("/balance/get")
    public Result getBalance() {
        User user = userService.findById(userService.getCurrentUser().getId());
        JSONObject data = new JSONObject();
        //总余额
        data.put("balance", user.getBalance()
                .add(user.getChargeBalance() == null ? BigDecimal.ZERO : user.getChargeBalance())
                .setScale(2, BigDecimal.ROUND_HALF_DOWN));
        data.put("virtualBalance", user.getVirtualBalance() == null ? 0 : user.getVirtualBalance());
        Integer totalCharm = user.getCharm() == null ? 0 : user.getCharm();
        Integer charmDrawSum = user.getCharmDrawSum() == null ? 0 : user.getCharmDrawSum();
        Integer leftCharm = totalCharm - charmDrawSum;
        data.put("charm", leftCharm);
        data.put("charmMoney", new BigDecimal(leftCharm)
                .multiply(Constant.CHARM_TO_MONEY_RATE).setScale(2, BigDecimal.ROUND_HALF_DOWN));
        //可提现余额
        data.put("drawsBalance", user.getBalance());
        //不可提现余额
        data.put("chargeBalance", user.getChargeBalance() == null ? 0 : user.getChargeBalance());
        return Result.success().data(data).msg("查询成功！");
    }
}
